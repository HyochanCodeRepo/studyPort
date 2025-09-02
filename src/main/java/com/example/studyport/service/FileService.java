package com.example.studyport.service;

import com.example.studyport.dto.ImageDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Log4j2
public class FileService {
    
    @Value("${imgLocation}")
    private String imgLocation;

    public ImageDTO register(MultipartFile multipartFile) throws IOException {
        log.info("파일 저장 시작: {}", multipartFile.getOriginalFilename());
        
        // 폴더가 존재하지 않으면 생성
        createDirectoryIfNotExists();
        
        // 원본 파일명 추출
        String oriImgName = multipartFile.getOriginalFilename();
        if (oriImgName.contains("/")) {
            oriImgName = oriImgName.substring(oriImgName.lastIndexOf("/") + 1);
        }
        
        // UUID를 사용한 고유한 파일명 생성
        UUID uuid = UUID.randomUUID();
        String imgName = uuid.toString() + "_" + oriImgName;
        String fileUploadPath = imgLocation + imgName;
        
        log.info("파일 저장 경로: {}", fileUploadPath);
        
        // 파일 저장
        try (FileOutputStream fos = new FileOutputStream(fileUploadPath)) {
            fos.write(multipartFile.getBytes());
            log.info("파일 저장 완료: {}", imgName);
        }
        
        // ImageDTO 생성 및 반환
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setOriImgName(oriImgName);
        imageDTO.setImgName(imgName);
        imageDTO.setImgUrl(imgLocation);

        return imageDTO;
    }

    public void del(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean deleted = file.delete();
            log.info("파일 삭제 {}: {}", deleted ? "성공" : "실패", path);
        } else {
            log.warn("삭제할 파일이 존재하지 않습니다: {}", path);
        }
    }
    
    private void createDirectoryIfNotExists() {
        try {
            Path path = Paths.get(imgLocation);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("이미지 저장 폴더 생성: {}", imgLocation);
            }
        } catch (IOException e) {
            log.error("이미지 저장 폴더 생성 실패: {}", imgLocation, e);
            throw new RuntimeException("이미지 저장 폴더를 생성할 수 없습니다.", e);
        }
    }
}
