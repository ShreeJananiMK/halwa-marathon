package com.tpSolar.halwaCityMarathon.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Component
public class ImageCompressUtil {

    public byte[] getCompressedImage (MultipartFile imageFile) throws Exception{

        BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
        // Compress and resize if needed
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int targetWidth = originalImage.getWidth();
        int targetHeight = originalImage.getHeight();
        float quality = 1.0f;

        do {
            outputStream.reset();
            // Resize and compress image
            Thumbnails.of(originalImage)
                    .size(targetWidth, targetHeight)  // Keeps original size unless adjusted
                    .outputQuality(quality)           // Reduce quality if needed
                    .outputFormat("jpeg")
                    .toOutputStream(outputStream);

            long size = outputStream.size();
            if (size <= 300 * 1024) {
                break;
            }

            // Reduce quality or dimensions gradually
            quality -= 0.05f;
            if (quality < 0.3f) {
                targetWidth = (int)(targetWidth * 0.9);
                targetHeight = (int)(targetHeight * 0.9);
                quality = 0.9f;
            }

        } while (outputStream.size() > 300 * 1024);

        return outputStream.toByteArray();

    }
}
