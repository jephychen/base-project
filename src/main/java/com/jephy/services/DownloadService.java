package com.jephy.services;

import com.jephy.utils.httpexceptions.InternalServerError500Exception;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by chenshijue on 2017/9/28.
 */

@Service
public class DownloadService {

    public void downloadFile(HttpServletResponse response, File downloadFile) {
        //设置header
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFile.getName());

        BufferedOutputStream outputStream = null;
        BufferedInputStream inputStream = null;
        try {
            outputStream = new BufferedOutputStream(response.getOutputStream());
            inputStream = new BufferedInputStream(new FileInputStream(downloadFile));

            byte[] buffer = new byte[10 * 1024];

            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerError500Exception("download file error");
        }
    }

}
