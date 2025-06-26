package com.example.studyport.service;

import com.example.studyport.repository.StudyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor

public class StudyServiceImpl implements StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper = new ModelMapper();
}
