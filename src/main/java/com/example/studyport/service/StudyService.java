package com.example.studyport.service;

import com.example.studyport.dto.StudyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StudyService {

    void createStudy(StudyDTO studyDTO);

    public void create(StudyDTO studyDTO, MultipartFile mainimg) throws IOException;
}
