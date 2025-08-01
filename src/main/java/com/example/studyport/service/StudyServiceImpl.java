package com.example.studyport.service;

import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Study;
import com.example.studyport.repository.StudyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor

public class StudyServiceImpl implements StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ImageService imageService;


    @Override
    public void create(StudyDTO studyDTO, MultipartFile mainimg) throws IOException {
        Study study =
            modelMapper.map(studyDTO, Study.class);

        studyRepository.save(study);

        if (mainimg != null && !mainimg.getOriginalFilename().isEmpty()) {
            imageService.register(mainimg, study.getId(), "Y");
        }



    }
}
