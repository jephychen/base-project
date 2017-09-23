package com.jephy.libs.json;

/**
 * Created by chenshijue on 2017/9/11.
 */

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.*;

/**
 *
 * 1:将JavaBean转换成Map、JSONObject
 * 2:将Map转换成Javabean
 * 3:将JSONObject转换成Map、Javabean
 *
 * @author Alexia
 */

public class JsonHelper {

    /**
     * 将Javabean转换为Map
     *
     * @param javaBean
     *            javaBean
     * @return Map对象
     */
    public static Map toMap(Object javaBean) {

        Map result = new HashMap();
        Method[] methods = javaBean.getClass().getDeclaredMethods();

        for (Method method : methods) {

            try {

                if (method.getName().startsWith("get")) {

                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);

                    Object value = method.invoke(javaBean, (Object[]) null);
                    result.put(field, null == value ? "" : value.toString());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return result;

    }

    /**
     * 将Json对象转换成Map
     *
     * @return Map对象
     * @throws JSONException
     */
    public static Map toMap(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        Object value = null;

        while (iterator.hasNext()) {

            key = (String) iterator.next();
            value = jsonObject.get(key);
            result.put(key, value);

        }
        return result;

    }

    /**
     * 将JavaBean转换成JSONObject（通过Map中转）
     *
     * @param bean
     *            javaBean
     * @return json对象
     */
    public static JSONObject toJSON(Object bean) {

        return new JSONObject(toMap(bean));

    }

    /**
     * 将Map转换成Javabean
     *
     * @param javabean
     *            javaBean
     * @param data
     *            Map数据
     */
    public static Object toJavaBean(Object javabean, Map data) throws Exception {

        Method[] methods = javabean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("set")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    method.invoke(javabean, new Object[] {
                            data.get(field)
                    });
                }
        }

        return javabean;

    }

    /**
     * JSONObject到JavaBean
     *
     * @return json对象
     * @throws ParseException
     *             json解析异常
     * @throws JSONException
     */
    public static void toJavaBean(Object javabean, String jsonString) throws Exception {

        JSONObject jsonObject = new JSONObject(jsonString);

        Map map = toMap(jsonObject.toString());

        toJavaBean(javabean, map);
    }

    /*
    * 此函数接受一个class类型和json字符串，返回指定class类型的对象。
    * 此函数可递归转换，但目前不能支持数组，需要使用数组可以用list代替。
    * */
    public static Object parse(Class targetClass, String jsonString) throws Exception {
        BasicDBObject jsonObj = (BasicDBObject) JSON.parse(jsonString);
        Object o = parseJsonObj(targetClass, jsonObj);
        return o;
    }

    private static Object parseJsonObj(Class targetType, BasicDBObject obj) throws Exception {
        if (obj == null) return null;

        Object o = targetType.newInstance();
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods){

            //只关注javabean的set方法,只处理接受一个参数的方法
            if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                Class paramType = method.getParameterTypes()[0];
                String field = getSetFieldName(method.getName());

                //如果是基本类型
                if (isPrimitive(paramType)){
                    if (obj.get(field) != null)
                        method.invoke(o, obj.get(field));
                }
                //如果是string类型
                else if (paramType.equals(String.class)){
                    method.invoke(o, obj.get(field));
                }
                //如果是List类型
                else if (paramType.equals(List.class)){
                    //根据字段名获取字段Field，进而得到list中元素类型的class
                    Field f = targetType.getDeclaredField(field);
                    ParameterizedType fieldType = (ParameterizedType) f.getGenericType();
                    Type elemType = fieldType.getActualTypeArguments()[0];
                    Class elemClass = Class.forName(elemType.getTypeName());

                    //从BasicDBList中获取元素，依次递归转换为目标类型并存入targetList
                    List targetList = new LinkedList();
                    BasicDBList elemList = (BasicDBList) obj.get(field);
                    if (elemList == null) continue;
                    for (Object elem : elemList){
                        if (isPrimitive(elemClass)|| elemClass.equals(String.class)){
                            targetList.add(elem);
                        }
                        else
                            targetList.add(parseJsonObj(elemClass, (BasicDBObject) elem));
                    }

                    //将转化好的元素list设置入对象中
                    method.invoke(o, targetList);
                }
                //如果是其他复杂类型
                else{
                    method.invoke(o, parseJsonObj(paramType, (BasicDBObject) obj.get(field)));
                }
            }
        }
        return o;
    }

    private static String getSetFieldName(String setMethodName){
        String field = setMethodName.substring(setMethodName.indexOf("set") + 3);
        field = field.toLowerCase().charAt(0) + field.substring(1);
        return field;
    }

    private static boolean isPrimitive(Class type){
        return type.isPrimitive()
                || type.getName().equals("java.lang.Boolean")
                || type.getName().equals("java.lang.Character")
                || type.getName().equals("java.lang.Integer")
                || type.getName().equals("java.lang.Byte")
                || type.getName().equals("java.lang.Short")
                || type.getName().equals("java.lang.Long")
                || type.getName().equals("java.lang.Float")
                || type.getName().equals("java.lang.Double");
    }

}
