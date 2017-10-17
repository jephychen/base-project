package com.jephy.services.io;

import com.jephy.libs.http.exceptions.Storage500Exception;
import com.jephy.libs.http.exceptions.StorageFileNotFound500Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class StorageService {

    private final Path rootLocation;

    public StorageService(@Value("${web.upload.path}") String path) {
        this.rootLocation = Paths.get(path);

        //如果目录不存在则新建
        File locationFile = rootLocation.toFile();
        if (!locationFile.exists()){
            locationFile.mkdirs();
        }
    }

    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new Storage500Exception("Failed to store empty file " + file.getOriginalFilename());
            }

            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new Storage500Exception("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    public void storeToPath(MultipartFile file, Path dest){
        try {
            if (file.isEmpty()) {
                throw new Storage500Exception("Failed to store empty file " + file.getOriginalFilename());
            }

            Files.copy(file.getInputStream(), dest.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new Storage500Exception("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new Storage500Exception("Failed to read stored files", e);
        }

    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFound500Exception("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFound500Exception("Could not read file: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new Storage500Exception("Could not initialize storage", e);
        }
    }

}
