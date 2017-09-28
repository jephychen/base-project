package com.jephy.controllers;

import com.jephy.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by chenshijue on 2017/9/28.
 */

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private StorageService storageService;

    @RequestMapping(method = RequestMethod.POST)
    public void uploadFile(@RequestParam("file") MultipartFile file){
        storageService.store(file);
    }

}
