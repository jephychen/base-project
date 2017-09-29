package com.jephy.controllers;

import com.jephy.aop.annotation.AuthCommon;
import com.jephy.services.DownloadService;
import com.jephy.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
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

    @Autowired
    private DownloadService downloadService;

    @RequestMapping(method = RequestMethod.GET)
    public List<String> filesList(){
        return storageService.loadAll().map(path -> path.getFileName().toString()).
                collect(Collectors.toList());
    }

    @AuthCommon
    @RequestMapping(value = "/{filename:.+}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable String filename, HttpServletResponse response){
        File file = storageService.load(filename).toFile();
        downloadService.downloadFile(response, file);
    }

    /*
    @AuthCommon
    @RequestMapping(method = RequestMethod.POST)
    public void uploadFile(@RequestParam("file") MultipartFile file){
        storageService.store(file);
    }
    */

    @AuthCommon
    @RequestMapping(method = RequestMethod.POST)
    public void uploadFiles(MultipartHttpServletRequest request){
        List<MultipartFile> files = request.getFiles("file");

        for (MultipartFile file : files){
            storageService.store(file);
        }
    }

}
