package com.imageprocess.controller;

import com.imageprocess.dto.ImageResponseDTO;
import com.imageprocess.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/")
    public ResponseEntity<?> getListOfImage(){
        return ResponseEntity.ok().body(service.getListOfImages());

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImage(@PathVariable long id){
        ImageResponseDTO imageResponseDTO = service.getImage(id);
        if(imageResponseDTO!=null){
            return ResponseEntity.ok().body(imageResponseDTO);
        }
        return ResponseEntity.notFound().build();

    }


}
