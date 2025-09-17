package com.imageprocess.service;

import com.imageprocess.dto.ImageResizeDTO;
import com.imageprocess.dto.ImageResponseDTO;

public interface ImageTransformService {
    public ImageResponseDTO resizeImage(long id ,ImageResizeDTO imageResizeDTO );
}
