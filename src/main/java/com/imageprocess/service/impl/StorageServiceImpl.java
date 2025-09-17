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

import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    public Map<String,String> uploadFile(MultipartFile file)  {
        Map<String,String> imageDetails = new HashMap<>();
        try{
            String filename = file.getOriginalFilename();
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
                URL url = storage.signUrl(blobInfo,15, TimeUnit.MINUTES,Storage.SignUrlOption.withV4Signature());

                imageDetails.put("url", String.valueOf(url));
                imageDetails.put("metadata", Objects.requireNonNull(blob.getMetadata()).toString());
                return imageDetails;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return imageDetails;
    }
}
