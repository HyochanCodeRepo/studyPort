package com.example.studyport.service;

import com.example.studyport.dto.ImageDTO;
import groovy.util.logging.Log4j2;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    public ImageDTO register(MultipartFile multipartFile, Long study_id,String repImgYn) throws IOException;
}
