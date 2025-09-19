package com.imageprocess.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.imageprocess.dto.ImageResizeDTO;
import com.imageprocess.dto.ImageResponseDTO;
import com.imageprocess.dto.ImageTransformDTO;
import com.imageprocess.model.Images;
import com.imageprocess.repository.ImagesRepository;
import com.imageprocess.service.ImageTransformService;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
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


//    @Override
//    public ImageResponseDTO resizeImage(long id,ImageResizeDTO imageResizeDTO) {
//
//        Optional<Images> images = imagesRepository.findById(id);
//
//        if(images.isPresent()) {
//            try {
//                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, images.get().getImageName()).build();
//                Blob blob = storage.get(bucketName,images.get().getImageName());
//                byte[] file = blob.getContent();
//                BufferedImage img = ImageIO.read(new ByteArrayInputStream(file));
//
//
//               BufferedImage resizedImg =  Thumbnails.of(img).size(imageResizeDTO.getWidth(),
//                       imageResizeDTO.getHeight()).asBufferedImage();
//
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                ImageIO.write(resizedImg, blob.getContentType().substring(6), byteArrayOutputStream);
//
//
//                byte[] transformedImg = byteArrayOutputStream.toByteArray();
//                Blob newBlob = storage.create(blobInfo, transformedImg);
//
//                if (newBlob != null && !newBlob.getContentType().isBlank()) {
//                    URL url = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature
//                         ());
//
//                    ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
//                    imageResponseDTO.setImageName(newBlob.getName());
//                    imageResponseDTO.setId(id);
//                    imageResponseDTO.setUrl(String.valueOf(url));
//
//                    return imageResponseDTO;
//
//                }
//
//            } catch (Exception e) {
//                log.error("e: ", e);
//                throw new RuntimeException(e);
//            }
//        }
//
//
//        return null;
//    }

    private BufferedImage convertBlobToBufferedImage(Blob blob){

        try {
            byte[] file = blob.getContent();
            return ImageIO.read(new ByteArrayInputStream(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private byte[] convertBufferedImageToByteArray(BufferedImage bufferedImage,String contentType){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, contentType, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }




    }

//    @Override
//    public ImageResponseDTO rotateImage(long id, ImageTransformDTO imageTransformDTO) {
//        Optional<Images> images = imagesRepository.findById(id);
//
//        if(images.isPresent()) {
//            try {
//                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, images.get().getImageName()).build();
//                Blob blob = storage.get(bucketName, images.get().getImageName());
//
//                BufferedImage img =  convertBlobToBufferedImage(blob);
//
//                BufferedImage transformedImg =
//                        Thumbnails.of(img).scale(1).rotate(imageTransformDTO.getRotation()).asBufferedImage();
//
//                byte[] imageToByteArray = convertBufferedImageToByteArray(transformedImg,blob.getContentType().substring(6));
//
//
//                Blob newBlob = storage.create(blobInfo, imageToByteArray);
//
//                if (newBlob != null && !newBlob.getContentType().isBlank()) {
//                    URL url = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature
//                            ());
//
//                    ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
//                    imageResponseDTO.setImageName(newBlob.getName());
//                    imageResponseDTO.setId(id);
//                    imageResponseDTO.setUrl(String.valueOf(url));
//
//                    return imageResponseDTO;
//
//                }
//
//            } catch (Exception e) {
//                log.error("e: ", e);
//                throw new RuntimeException(e);
//            }
//        }
//
//
//        return null;
//    }

    @Override
    public ImageResponseDTO transformImage(long id, ImageTransformDTO imageTransformDTO) {
        Optional<Images> images = imagesRepository.findById(id);

        if (images.isPresent()) {
            try {
                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, images.get().getImageName()).build();
                Blob blob = storage.get(bucketName, images.get().getImageName());
                BufferedImage img =  convertBlobToBufferedImage(blob);

                // Apply transformations
                if (imageTransformDTO.getTransformations().getResize() != null) {
                    ImageTransformDTO.Transformations.Resize resize = imageTransformDTO.getTransformations().getResize();
                    img = Thumbnails.of(img).size(resize.getWidth(), resize.getHeight()).asBufferedImage();
                }

                if (imageTransformDTO.getTransformations().getCrop() != null) {
                    ImageTransformDTO.Transformations.Crop crop = imageTransformDTO.getTransformations().getCrop();
                    img = Thumbnails.of(img).crop(Positions.CENTER)
                            .size(crop.getWidth(), crop.getHeight())
                            .asBufferedImage();
                }

                if (imageTransformDTO.getTransformations().getRotate()!=0) {
                    img = Thumbnails.of(img).scale(1).rotate(
                            imageTransformDTO.getTransformations().getRotate()).asBufferedImage();
                }


                // Convert the transformed image back to byte array
                String fileType = blob.getContentType().substring(6);

                if(imageTransformDTO.getTransformations().getFormat() !=null){

                    String fileFormat = "image/"+imageTransformDTO.getTransformations().getFormat();
                    Map<String, String> metadata = new HashMap<>(Objects.requireNonNull(blob.getMetadata()));


                    String newFileType = imageTransformDTO.getTransformations().getFormat();

                    String newFileName = images.get().getImageName().replace(fileType,newFileType);

                    fileType = newFileType;


                    metadata.replace("content-type", fileFormat);

                    storage.delete(blob.getBlobId());

                    blobInfo =
                            BlobInfo.newBuilder(bucketName,newFileName)
                                    .setContentType(fileFormat)
                                    .setMetadata(metadata)
                                    .build();

                    images.get().setImageName(newFileName);

                    imagesRepository.save(images.get());



                }
                byte[] transformedImg = convertBufferedImageToByteArray(img, fileType);


                Blob newBlob = storage.create(blobInfo, transformedImg);

                if (newBlob != null && !newBlob.getContentType().isBlank()) {
                    URL url = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());

                    ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
                    imageResponseDTO.setImageName(newBlob.getName());
                    imageResponseDTO.setId(id);
                    imageResponseDTO.setUrl(String.valueOf(url));

                    return imageResponseDTO;
                }
            } catch (Exception e) {
                log.error("Error transforming image: ", e);
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException("Image not found for ID: " + id);
    }
}
