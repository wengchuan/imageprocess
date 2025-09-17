package com.imageprocess.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.imageprocess.dto.ImageResponseDTO;
import com.imageprocess.model.Images;
import com.imageprocess.model.User;
import com.imageprocess.repository.ImagesRepository;
import com.imageprocess.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.BlobInfo;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class StorageServiceImpl implements StorageService {

    private final Storage storage;
    private final String bucketName;
    private final ImagesRepository imagesRepository;

    @Autowired
    public StorageServiceImpl(Storage storage,
                              @Value("${bucket_name}") String bucketName, ImagesRepository imagesRepository) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.imagesRepository = imagesRepository;
    }

    @Override
    public Map<String,String> uploadFile(MultipartFile file)  {


        Map<String,String> imageDetails = new HashMap<>();
        try{
            String filename = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes();
            String randomFilename = UUID.randomUUID().toString() + "_" + filename;
            long fileSize = file.getSize();
            String contentType = file.getContentType();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User userDetails = (User) authentication.getPrincipal();


            Map<String,String> metadata = new HashMap<>();
            metadata.put("filename",filename);
            metadata.put("content-type",contentType);
            metadata.put("content-length",String.valueOf(fileSize));
            metadata.put("userId:", String.valueOf(userDetails.getId()));



            BlobInfo blobInfo = BlobInfo
                    .newBuilder(bucketName, randomFilename)
                    .setContentType(contentType)
                    .setMetadata(metadata)
                    .build();

            final Blob blob = storage.create(blobInfo,fileBytes);



            if(blob!=null && !blob.getContentType().isBlank()){
                URL url = storage.signUrl(blobInfo,15, TimeUnit.MINUTES,Storage.SignUrlOption.withV4Signature());
                imageDetails.put("url", String.valueOf(url));
                imageDetails.put("metadata", Objects.requireNonNull(blob.getMetadata()).toString());

                //Add it to database
                Images images = new Images();
                images.setImageName(blob.getName());
                images.setImageOriName(filename);
                images.setUser(userDetails);

                imagesRepository.save(images);

                return imageDetails;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return imageDetails;
    }

    @Override
    public List<ImageResponseDTO> getListOfImages() {
        List<ImageResponseDTO> imageResponseDTOList = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetail = (User) authentication.getPrincipal();
        List<Images> imagesList = imagesRepository.findByUserId(userDetail.getId());


        try {
            imagesList.forEach((image) -> {
                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName,image.getImageName()).build();
                URL url = storage.signUrl(blobInfo,
                        15,
                        TimeUnit.MINUTES,
                        Storage.SignUrlOption.withV4Signature());

                ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
                imageResponseDTO.setId(image.getId());
                imageResponseDTO.setImageName(image.getImageName());
                imageResponseDTO.setUrl(String.valueOf(url));


                imageResponseDTOList.add(imageResponseDTO);
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return imageResponseDTOList;
    }

    @Override
    public ImageResponseDTO getImage(long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        ImageResponseDTO imageResponseDTO = new ImageResponseDTO();


        Images image = imagesRepository.findByUserIdAndId(user.getId(), id);
        if(image!=null) {

            imageResponseDTO.setImageName(image.getImageName());
            imageResponseDTO.setId(image.getId());

            try {
                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, image.getImageName()).build();
                URL url = storage.signUrl(blobInfo,
                        15,
                        TimeUnit.MINUTES,
                        Storage.SignUrlOption.withV4Signature());

                imageResponseDTO.setUrl(String.valueOf(url));
                return imageResponseDTO;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }


}
