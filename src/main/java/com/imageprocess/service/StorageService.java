package com.imageprocess.service;

import com.imageprocess.dto.ImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StorageService {

    public Map<String,String> uploadFile(MultipartFile file) throws Exception;
    public List<ImageResponseDTO> getListOfImages();
    public ImageResponseDTO getImage(long id);

}
