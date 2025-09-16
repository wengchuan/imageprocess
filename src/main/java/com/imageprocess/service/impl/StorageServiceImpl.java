package com.imageprocess.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.imageprocess.service.StorageService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.BlobInfo;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class StorageServiceImpl implements StorageService {

    private final Storage storage;
    private final String bucketName;

    @Autowired
    public StorageServiceImpl(Storage storage,
                              @Value("${bucket_name}") String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(MultipartFile file)  {
        try{
            String filename = file.getName();
            byte[] fileBytes = file.getBytes();
            long fileSize = file.getSize();
            String contentType = file.getContentType();




            Map<String,String> metadata = new HashMap<>();
            metadata.put("filename",filename);
            metadata.put("content-type",contentType);
            metadata.put("content-length",String.valueOf(fileSize));



            BlobInfo blobInfo = BlobInfo
                    .newBuilder(bucketName, Objects.requireNonNull(file.getOriginalFilename()))
                    .setContentType(contentType)
                    .setMetadata(metadata)
                    .build();

            final Blob blob = storage.create(blobInfo,fileBytes);

            if(blob!=null && !blob.getContentType().isBlank()){
                return metadata.toString();
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "failed to upload";
    }
}
