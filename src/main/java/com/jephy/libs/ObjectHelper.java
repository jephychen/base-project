package com.jephy.libs;

import java.lang.reflect.Method;

/**
 * Created by chenshijue on 2017/9/22.
 */
public class ObjectHelper {

    /*
    * 如果newObj属性不为null，则替换oldObj中的相同属性
    * */
    public static void updateObj(Object oldObj, final Object newObj) throws Exception {
        //只处理同一个类型的对象
        if (oldObj.getClass() != newObj.getClass())
            return;

        Class clazz = oldObj.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods){
            if (method.getName().startsWith("get")){
                Object fieldValueNew = method.invoke(newObj);

                String setMethodName = setMethodName(method.getName());
                Method setMethod = clazz.getMethod(setMethodName, method.getReturnType());

                if (fieldValueNew != null){
                    setMethod.invoke(oldObj, fieldValueNew);
                }
            }
        }
    }

    private static String setMethodName(String getMethodName){
        return getMethodName.replaceFirst("get", "set");
    }

    private static String getGetFieldName(String setMethodName){
        String field = setMethodName.substring(setMethodName.indexOf("get") + 3);
        field = field.toLowerCase().charAt(0) + field.substring(1);
        return field;
    }

}
