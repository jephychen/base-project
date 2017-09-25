package com.jephy.libs;

import com.mongodb.BasicDBObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshijue on 2017/9/13.
 */
public class JwtHelper {

    /**
     * @param body 需要进行jwt的内容
     * @param timeout 多少秒之后超时
     * @return jwt字符串
     * */
    public static String genJwt(Map<String, String> body, long timeout) throws UnsupportedEncodingException {
        Map<String, String> header = getDefaultHeader();
        header.put("exp", String.valueOf(System.currentTimeMillis() + timeout * 1000));

        String header64str = EncryptHelper.base64EncodeMap(header);
        String body64str = EncryptHelper.base64EncodeMap(body);

        String toEncrypt = header64str + "." + body64str;
        String sign = EncryptHelper.encrypt(toEncrypt, Const.JWT_KEY);

        return toEncrypt + "." + sign;
    }

    /**
     * @param jwt 需要解析的jwt字符串
     * @return 返回payload接口，如果jwt超时或者非法，则为null
     * */
    public static BasicDBObject getPayload(String jwt) throws IOException {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3)
            return null;

        String toEncrypt = parts[0] + "." + parts[1];
        String mySign = EncryptHelper.encrypt(toEncrypt, Const.JWT_KEY);
        if (mySign.equals(parts[2])){
            BasicDBObject header = EncryptHelper.base64Decode(parts[0]);
            String expired = header.getString("exp");
            if (System.currentTimeMillis() > Long.parseLong(expired)){
                return null;
            }
            return EncryptHelper.base64Decode(parts[1]);
        }

        return null;
    }

    private static Map<String, String> getDefaultHeader(){
        Map<String, String> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        return header;
    }

}
