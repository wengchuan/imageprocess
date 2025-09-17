package com.imageprocess.controller;

import com.imageprocess.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/api/image")
public class ImageStorageController {
    private final StorageService service;


    @Autowired
    public ImageStorageController(StorageService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleUpload(@RequestParam("file") MultipartFile file) throws Exception {

        Map<String,String> imageDetails = service.uploadFile(file);

       return ResponseEntity.ok().body(imageDetails);
    }


}
