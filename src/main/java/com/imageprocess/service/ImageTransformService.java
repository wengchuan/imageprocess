package com.imageprocess.service;

import com.imageprocess.dto.ImageResizeDTO;
import com.imageprocess.dto.ImageResponseDTO;
import com.imageprocess.dto.ImageTransformDTO;

public interface ImageTransformService {
    //public ImageResponseDTO resizeImage(long id ,ImageResizeDTO imageResizeDTO );
    //public ImageResponseDTO rotateImage(long id, ImageTransformDTO imageTransformDTO);
    public ImageResponseDTO transformImage(long id, ImageTransformDTO imageTransformDTO);

}
