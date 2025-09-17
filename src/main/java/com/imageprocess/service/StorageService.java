package com.imageprocess.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface StorageService {

    public Map<String,String> uploadFile(MultipartFile file) throws Exception;

}
