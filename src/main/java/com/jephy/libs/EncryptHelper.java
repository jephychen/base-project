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
import java.util.Map;

/**
 * Created by chenshijue on 2017/9/25.
 */
public class EncryptHelper {

    /**
     * 将map进行base64加密
     * */
    public static String base64EncodeMap(Map<String, String> json) throws UnsupportedEncodingException {
        String jsonStr = JSON.serialize(json);
        String s = new BASE64Encoder().encode(jsonStr.getBytes("utf-8"));
        return s;
    }

    /**
     * 对字符串进行base64解密
     * */
    public static BasicDBObject base64Decode(String str) throws IOException {
        byte[] jsonByte = new BASE64Decoder().decodeBuffer(str);
        String jsonStr = new String(jsonByte, "utf-8");
        return (BasicDBObject) JSON.parse(jsonStr);
    }

    /**
     * 对字符串进行SHA-256加密
     * */
    public static String encrypt(String target, String key){
        return HMACSHA256(target.getBytes(), key.getBytes());
    }

    public static String HMACSHA256(byte[] data, byte[] key)
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
