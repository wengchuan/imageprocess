package com.imageprocess.controller;

import com.imageprocess.dto.ImageResizeDTO;
import com.imageprocess.dto.ImageResponseDTO;
import com.imageprocess.service.ImageTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/image")
public class ImageTransformController {
    private final ImageTransformService imageTransformService;


    @Autowired
    public ImageTransformController(ImageTransformService imageTransformService) {
        this.imageTransformService = imageTransformService;
    }

    @PostMapping("/{id}/resize")
    public ResponseEntity<?> imageResize(@PathVariable long id, @RequestBody ImageResizeDTO imageResizeDTO){
       return ResponseEntity.ok().body(imageTransformService.resizeImage(id, imageResizeDTO));
    }

}
