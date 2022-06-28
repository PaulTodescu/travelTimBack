package com.travelTim.files;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ImageService {

    private final Storage storage;

    @Autowired
    public ImageService(Storage storage) {
        this.storage = storage;
    }

    public void uploadUserImage(Long userId, Optional<MultipartFile> image) throws IOException {
        String uploadPath = "user/" + userId;
        if (image.isPresent()) {
            Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
            if (blobs.getValues().iterator().hasNext()) {
                blobs.getValues().iterator().next().delete();
            }
            BlobId blobId = BlobId.of("traveltim-images", uploadPath + "/" + image.get().getOriginalFilename());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            byte[] arr = image.get().getBytes();
            storage.create(blobInfo, arr);
        } else {
            this.uploadDefaultUserImage(userId);
        }
    }

    public void uploadDefaultUserImage(Long userId) {
        String defaultImageName = "default.png";
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix("user/"));
        for (Blob blob: blobs.getValues()) {
            String fileName = this.getFileNameFromPath(blob.getName());
            if (fileName.equals(defaultImageName)) {
                BlobId blobId = BlobId.of("traveltim-images", "user/" + userId + "/" + defaultImageName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                byte[] arr = blob.getContent();
                storage.create(blobInfo, arr);
                return;
            }
        }
    }

    public String getUserImage(Long userId){
        String uploadPath = "user/" + userId;
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        Blob imageBlob = blobs.getValues().iterator().next();
        return imageBlob.getMediaLink();
    }

    public void deleteUserImage(Long userId) {
        String uploadPath = "user/" + userId;
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        if (blobs.getValues().iterator().hasNext()) {
            blobs.getValues().forEach(Blob::delete);
        }
    }

    public void uploadBusinessImages(Long businessId, Optional<List<MultipartFile>> images) throws IOException {
        String uploadPath = "business/" + businessId;
        if (images.isPresent()) {
            Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
            if (blobs.getValues().iterator().hasNext()) {
                blobs.getValues().forEach(Blob::delete);
            }
            for (MultipartFile image : images.get()) {
                BlobId blobId = BlobId.of("traveltim-images", uploadPath + "/" + image.getOriginalFilename());
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                byte[] arr = image.getBytes();
                storage.create(blobInfo, arr);
            }
        } else {
            this.uploadDefaultBusinessImage(businessId);
        }
    }

    public void uploadDefaultBusinessImage(Long businessId) {
        String defaultImageName = "default.png";
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix("business/"));
        for (Blob blob: blobs.getValues()) {
            String fileName = this.getFileNameFromPath(blob.getName());
            if (fileName.equals(defaultImageName)) {
                BlobId blobId = BlobId.of("traveltim-images", "business/" + businessId + "/" + defaultImageName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                byte[] arr = blob.getContent();
                storage.create(blobInfo, arr);
                return;
            }
        }
    }

    public List<String> getBusinessImages(Long businessId){
        List<String> images = new ArrayList<>();
        String uploadPath = "business/" + businessId;
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        for (Blob imageBlob: blobs.getValues()) {
            images.add(imageBlob.getMediaLink());
        }
        return images;
    }

    public String getBusinessFrontImage(Long businessId){
        String uploadPath = "business/" + businessId;
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        if (blobs.getValues().iterator().hasNext()) {
            return blobs.getValues().iterator().next().getMediaLink();
        }
        return "";
    }

    public List<String> getBusinessImagesNames(Long businessId){
        List<String> imageNames = new ArrayList<>();
        String uploadPath = "business/" + businessId;
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        for (Blob imageBlob: blobs.getValues()) {
            imageNames.add(this.getFileNameFromPath(imageBlob.getName()));
        }
        return imageNames;
    }

    public void deleteBusinessImages(Long businessId) {
        String uploadPath = "business/" + businessId;
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        if (blobs.getValues().iterator().hasNext()) {
            blobs.getValues().forEach(Blob::delete);
        }
    }

    public void uploadOfferImages(Long offerId, List<MultipartFile> images, String offerType) throws IOException {
        String uploadPath = this.getOfferUploadPath(offerType, offerId);
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        if (blobs.getValues().iterator().hasNext()) {
            blobs.getValues().forEach(Blob::delete);
        }
        for (MultipartFile image : images) {
            BlobId blobId = BlobId.of("traveltim-images", uploadPath + "/" + image.getOriginalFilename());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            byte[] arr = image.getBytes();
            storage.create(blobInfo, arr);
        }
    }

    public List<String> getOfferImages(String offerType, Long offerId){
        List<String> images = new ArrayList<>();
        String uploadPath = this.getOfferUploadPath(offerType, offerId);
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        for (Blob imageBlob: blobs.getValues()) {
            System.out.println(imageBlob.getMediaLink());
            images.add(imageBlob.getMediaLink());
        }
        return images;
    }

    public String getOfferFrontImage(String offerType, Long offerId){
        String uploadPath = this.getOfferUploadPath(offerType, offerId);
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        if (blobs.getValues().iterator().hasNext()) {
            return blobs.getValues().iterator().next().getMediaLink();
        }
        return "";
    }

    public List<String> getOfferImagesBase64(String offerType, Long offerId){
        List<String> images = new ArrayList<>();
        String uploadPath = this.getOfferUploadPath(offerType, offerId);
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        for (Blob imageBlob: blobs.getValues()) {
            String encodeBase64 = Base64.getEncoder().encodeToString(imageBlob.getContent());
            String image = "data:image/" + this.getFileNameFromPath(imageBlob.getName()) + ";base64," + encodeBase64;
            images.add(image);
        }
        return images;
    }

    public List<String> getOfferImagesNames(String offerType, Long offerId) {
        List<String> images = new ArrayList<>();
        String uploadPath = this.getOfferUploadPath(offerType, offerId);
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        for (Blob imageBlob: blobs.getValues()) {
            images.add(this.getFileNameFromPath(imageBlob.getName()));
        }
        return images;
    }

    public void deleteOfferImages(String offerType, Long offerId) {
        String uploadPath = this.getOfferUploadPath(offerType, offerId);
        Page<Blob> blobs = storage.list("traveltim-images", Storage.BlobListOption.prefix(uploadPath));
        if (blobs.getValues().iterator().hasNext()) {
            blobs.getValues().forEach(Blob::delete);
        }
    }

    public String getFileNameFromPath(String path) {
        return Paths.get(path).getFileName().toString();
    }

    public String getOfferUploadPath(String offerType, Long offerId){
        String basePath = "offer";
        switch (offerType){
            case "lodging":
                return basePath + "/lodging/" + offerId;
            case "food":
                return basePath + "/food/" + offerId;
            case "attractions":
                return basePath + "/attractions/" + offerId;
            case "activities":
                return basePath + "/activities/" + offerId;
            default:
                return "";
        }
    }

}
