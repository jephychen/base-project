package com.jephy.controllers;

import com.jephy.aop.annotation.AuthCommon;
import com.jephy.libs.http.FileDownloadHelper;
import com.jephy.services.storage.StorageService;
import com.jephy.utils.httpexceptions.InternalServerError500Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chenshijue on 2017/9/28.
 */

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private StorageService storageService;

    @RequestMapping(method = RequestMethod.GET)
    public List<String> filesList(){
        return storageService.loadAll().map(path -> path.getFileName().toString()).
                collect(Collectors.toList());
    }

    @AuthCommon
    @RequestMapping(value = "/{filename:.+}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable String filename, HttpServletResponse response){
        File file = storageService.load(filename).toFile();
        try {
            FileDownloadHelper.downloadFile(response, file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerError500Exception("file download error");
        }
    }

    @AuthCommon
    @RequestMapping(method = RequestMethod.POST)
    public void uploadFile(@RequestParam("file") MultipartFile file){
        storageService.store(file);
    }

}
