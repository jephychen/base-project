package com.jephy.libs;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshijue on 2017/9/13.
 */
public class JwtHelper {

    private final static String JWT_KEY = "chemixnodie+";

    public static String genJwt(Map<String, String> body, long timeout) throws UnsupportedEncodingException {
        Map<String, String> header = getDefaultHeader();
        header.put("exp", String.valueOf(System.currentTimeMillis() + timeout * 1000));

        String header64str = base64EncodeMap(header);
        String body64str = base64EncodeMap(body);

        String toEncrypt = header64str + "." + body64str;
        String sign = encrypt(toEncrypt);

        return toEncrypt + "." + sign;
    }

    public static BasicDBObject getPayload(String jwt) throws IOException {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3)
            return null;

        String toEncrypt = parts[0] + "." + parts[1];
        String mySign = encrypt(toEncrypt);
        if (mySign.equals(parts[2])){
            BasicDBObject header = base64Decode(parts[0]);
            String expired = header.getString("exp");
            if (System.currentTimeMillis() > Long.parseLong(expired)){
                return null;
            }
            return base64Decode(parts[1]);
        }

        return null;
    }

    private static Map<String, String> getDefaultHeader(){
        Map<String, String> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        return header;
    }

    private static String base64EncodeMap(Map<String, String> json) throws UnsupportedEncodingException {
        String jsonStr = JSON.serialize(json);
        String s = new BASE64Encoder().encode(jsonStr.getBytes("utf-8"));
        return s;
    }

    private static BasicDBObject base64Decode(String str) throws IOException {
        byte[] jsonByte = new BASE64Decoder().decodeBuffer(str);
        String jsonStr = new String(jsonByte, "utf-8");
        return (BasicDBObject) JSON.parse(jsonStr);
    }

    private static String encrypt(String target){
        return HMACSHA256(target.getBytes(), JWT_KEY.getBytes());
    }

    private static String HMACSHA256(byte[] data, byte[] key)
    {
        try  {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] b)
    {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

}
