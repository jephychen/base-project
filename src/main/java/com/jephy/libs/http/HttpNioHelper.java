package com.jephy.libs.http;

import com.jephy.libs.http.exceptions.ServiceUnavailable503Exception;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

/**
 * Created by chenshijue on 2017/10/17.
 */
public class HttpNioHelper {

    /**
     * send GET request to url
     * @param url target address
     * @param params parameters ordered by map
     * @return result in string
     * */
    public static String get(String url, Map<String, String> params) throws ExecutionException,
            InterruptedException, IOException {
        String urlWithParams = buildUrl(url, params);

        HttpGet request = new HttpGet(urlWithParams);
        request.setHeader("Accept-Encoding", "gzip");
        HttpResponse response = sendRequest(request);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300)
            return getJsonStringFromGZIP(response.getEntity().getContent());

        throw new ServiceUnavailable503Exception("external service unavailable");
    }

    /**
     * send POST request to url
     * @param url target address
     * @param params parameters ordered by map
     * @return result in string
     * */
    public static String post(String url, Map<String, String> params) throws IOException,
            ExecutionException, InterruptedException {
        HttpPost request = createHttpPost(url, params);
        request.setHeader("Accept-Encoding", "gzip");
        HttpResponse response = sendRequest(request);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300)
            return getJsonStringFromGZIP(response.getEntity().getContent());

        throw new ServiceUnavailable503Exception("external service unavailable");
    }

    /**
     * send POST request to url
     * @param url target address
     * @param params parameters in string, such as json string
     * @return result in string
     * */
    public static String post(String url, String params) throws IOException, InterruptedException,
            ExecutionException {
        HttpPost request = createHttpPost(url, null);
        request.setHeader("Accept-Encoding", "gzip");
        request.setEntity(new StringEntity(params, HTTP.UTF_8));
        HttpResponse response = sendRequest(request);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300)
            return getJsonStringFromGZIP(response.getEntity().getContent());

        throw new ServiceUnavailable503Exception("external service unavailable");
    }

    /**
     * send POST request to url, parameters in xml format, get xml result
     * @param url target address
     * @param xmlData parameters in string, conforming xml format
     * @return result in xml Document
     * */
    public static Document postXml(String url, String xmlData) throws IOException, InterruptedException,
            ExecutionException, DocumentException {
        HttpPost request = createHttpPost(url, null);
        request.setHeader("Pragma", "no-cache");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Content-Type", "text/xml");
        if (xmlData != null)
            request.setEntity(new ByteArrayEntity(xmlData.getBytes("utf-8")));

        HttpResponse response = sendRequest(request);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300){
            SAXReader reader = new SAXReader();
            return reader.read(new InputStreamReader(response.getEntity().getContent()));
        }

        throw new ServiceUnavailable503Exception("external service unavailable");
    }

    protected static HttpResponse sendRequest(final HttpUriRequest request) throws ExecutionException,
            InterruptedException, IOException {
        CloseableHttpAsyncClient httpAsyncClient = null;
        try{
            httpAsyncClient = HttpAsyncClients.createDefault();
            httpAsyncClient.start();

            Future<HttpResponse> futureResponse = httpAsyncClient.execute(request, null);
            HttpResponse response = futureResponse.get();
            return response;
        }
        finally {
            if (httpAsyncClient != null)
                httpAsyncClient.close();
        }
    }

    private static String getJsonStringFromGZIP(InputStream is) throws IOException {
        String jsonString = null;

        BufferedInputStream bis = new BufferedInputStream(is);
        bis.mark(2);
        // 取前两个字节
        byte[] header = new byte[2];
        int result = bis.read(header);
        // reset 输入流到开始位置
        bis.reset();
        // 判断是否是 GZIP 格式
        int headerData = getShort(header);
        // Gzip 流 的前两个字节是 0x1f8b
        if (result != -1 && headerData == 0x1f8b) {
            // LogUtil.i("HttpTask", " use GZIPInputStream  ");
            is = new GZIPInputStream(bis);
        } else {
            // LogUtil.d("HttpTask", " not use GZIPInputStream");
            is = bis;
        }
        InputStreamReader reader = new InputStreamReader(is, "utf-8");
        char[] data = new char[100];
        int readSize;
        StringBuffer sb = new StringBuffer();
        while ((readSize = reader.read(data)) > 0) {
            sb.append(data, 0, readSize);
        }
        jsonString = sb.toString();
        bis.close();
        reader.close();

        return jsonString;
    }

    private static int getShort(byte[] data) {
        return (data[0] << 8) | data[1] & 0xFF;
    }

    public static String buildUrl(String reqUrl, Map<String, String> params) {
        if (params == null) return reqUrl;

        StringBuilder query = new StringBuilder();
        Set<String> set = params.keySet();
        for (String key : set) {
            query.append(String.format("%s=%s&", key, params.get(key)));
        }
        return reqUrl + "?" + query.toString();
    }

    public static HttpPost createHttpPost(String url, Map<String, String> params) throws UnsupportedEncodingException {
        HttpPost request = new HttpPost(url);
        if (params == null)
            return request;

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key)));
        }

        request.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        return request;
    }

}
