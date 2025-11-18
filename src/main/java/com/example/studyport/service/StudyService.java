package com.example.studyport.service;

import com.example.studyport.dto.StudyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StudyService {

    void createStudy(StudyDTO studyDTO);

    public void create(StudyDTO studyDTO, MultipartFile mainimg) throws IOException;

    // 검색 기능
    List<StudyDTO> searchByKeyword(String keyword);

    // 전체 스터디 조회
    List<StudyDTO> getAllStudies();
}
