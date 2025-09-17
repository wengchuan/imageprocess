package com.imageprocess.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.imageprocess.dto.ImageResizeDTO;
import com.imageprocess.dto.ImageResponseDTO;
import com.imageprocess.model.Images;
import com.imageprocess.repository.ImagesRepository;
import com.imageprocess.service.ImageTransformService;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ImageTransformServiceImpl implements ImageTransformService {

    private static final Logger log = LogManager.getLogger(ImageTransformServiceImpl.class);
    private final Storage storage;
    private final String bucketName;
    private final ImagesRepository imagesRepository;

    public ImageTransformServiceImpl(Storage storage, @Value("${bucket-name}") String bucketName, ImagesRepository imagesRepository) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.imagesRepository = imagesRepository;
    }


    @Override
    public ImageResponseDTO resizeImage(long id,ImageResizeDTO imageResizeDTO) {

        Optional<Images> images = imagesRepository.findById(id);

        if(images.isPresent()) {
            try {
                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, images.get().getImageName()).build();
                Blob blob = storage.get(bucketName,images.get().getImageName());
                byte[] file = blob.getContent();
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(file));


               BufferedImage resizedImg =  Thumbnails.of(img).size(imageResizeDTO.getWidth(),
                       imageResizeDTO.getHeight()).asBufferedImage();


                Map<String, String> imageDetails = new HashMap<>();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(resizedImg, blob.getContentType().substring(6), byteArrayOutputStream);


                byte[] transformedImg = byteArrayOutputStream.toByteArray();
                Blob newBlob = storage.create(blobInfo, transformedImg);

                if (newBlob != null && !newBlob.getContentType().isBlank()) {
                    URL url = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature
                         ());

                    ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
                    imageResponseDTO.setImageName(newBlob.getName());
                    imageResponseDTO.setId(id);
                    imageResponseDTO.setUrl(String.valueOf(url));

                    return imageResponseDTO;

                }

            } catch (Exception e) {
                log.error("e: ", e);
                throw new RuntimeException(e);
            }
        }


        return null;
    }
}
