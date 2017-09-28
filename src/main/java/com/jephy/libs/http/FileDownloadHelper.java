package com.jephy.libs.http;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by chenshijue on 2017/9/28.
 */
public class FileDownloadHelper {

    public static void downloadFile(HttpServletResponse response, File downloadFile) throws IOException {
        //设置header
        response.setHeader("content-type" , "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFile.getName());

        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(downloadFile));

        byte[] buffer = new byte[10 * 1024];

        int length = 0;
        while ((length = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }

}
