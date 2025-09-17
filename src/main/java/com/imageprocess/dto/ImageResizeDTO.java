package com.imageprocess.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageResizeDTO {
    private final int width;
    private final int height;


    public ImageResizeDTO(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
