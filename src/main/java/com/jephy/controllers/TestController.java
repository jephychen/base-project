package com.jephy.controllers;

import com.jephy.libs.http.HttpNioHelper;
import com.jephy.libs.http.exceptions.InternalServerError500Exception;
import org.dom4j.DocumentException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by chenshijue on 2017/10/17.
 */

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public String test(){
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        Map<String, String> params = new HashMap<>();
        params.put("limit", "2");
        params.put("page", "1");
        try {
            return HttpNioHelper.postXml(url, "<xml></xml>").toString();
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new InternalServerError500Exception("send request failed");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new InternalServerError500Exception("send request interrupted");
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerError500Exception("IO error");
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new InternalServerError500Exception("parse xml error");
        }
    }

}
