package com.example.studyport.service;

import com.example.studyport.dto.ImageDTO;
import groovy.util.logging.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


@Service
@Log4j2
public class FileService {
    @Value("C:/ex/")
    String imgLocation;

    public ImageDTO register(MultipartFile multipartFile) throws IOException {
        String oriImgName = multipartFile.getOriginalFilename()
                .substring(multipartFile.getOriginalFilename().lastIndexOf("/") + 1);

        UUID uuid = UUID.randomUUID();

        String imgName = uuid.toString() + "_"  + oriImgName;
        String fileUploadPath = imgLocation + imgName;
        FileOutputStream fos = new FileOutputStream(fileUploadPath);

        fos.write(multipartFile.getBytes());
        fos.close();
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setOriImgName(oriImgName);
        imageDTO.setImgName(imgName);
        imageDTO.setImgUrl(imgLocation);

        return imageDTO;

    }

    public void del(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

    }

}
