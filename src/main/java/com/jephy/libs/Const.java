package com.jephy.libs;

/**
 * Created by chenshijue on 2017/9/25.
 */
public interface Const {

     /*
     * jwt加密密钥
     * */
     final static String JWT_KEY = "chemixnodie+";

     /*
     * 密码加密密钥
     * */
     final static String PASSWORD_KEY = "chemixnodRTET#453#$+";

     /*
     * jwt的cookie名称
     * **/
     final static String JWT_COOKIE_NAME = "jwt";


     /*
     * cookie最大存活时间
     * **/
     final static int MAX_SESSION_EXPIRE = 60 * 60 * 24 * 30;

}
