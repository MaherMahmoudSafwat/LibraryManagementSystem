package com.LibraryManagementSystem.AiSystems.Utility;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class Images
{
    public String SaveAndUploadFilesToHardDisk(String UploadFileName, MultipartFile Image) throws IOException
    {
        ValidateInputImage(Image);
        String UploadNewFileName = System.currentTimeMillis() + "-" + Image.getOriginalFilename();
        String FullPath = UploadFileName + UploadNewFileName;
        File Directory = new File(UploadFileName);
        if(!Directory.exists())
        {
            Directory.mkdirs();
        }

        Image.transferTo(new File(FullPath));
        return UploadNewFileName;
    }

    public static void ValidateInputImage(MultipartFile Image)
    {
        if(Image.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Image too large. Max 5MB");
        }

        String contentType = Image.getContentType();
        List<String> allowedTypes = Arrays.asList(
                "image/jpeg", "image/jpg", "image/png", "image/gif"
        );

        if(!allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, and GIF images allowed");
        }

        String filename = Image.getOriginalFilename();
        if(filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        if(filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename characters");
        }
    }
}
