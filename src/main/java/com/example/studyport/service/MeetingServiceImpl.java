package com.example.studyport.service;

import com.example.studyport.dto.MeetingDTO;
import com.example.studyport.entity.Meeting;
import com.example.studyport.entity.Members;
import com.example.studyport.entity.Study;
import com.example.studyport.repository.MeetingRepository;
import com.example.studyport.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class MeetingServiceImpl implements MeetingService {

    private ModelMapper modelMapper = new ModelMapper();
    private final MeetingRepository meetingRepository;
    private final StudyRepository studyRepository;



    /*const title = document.getElementById('meetingTitle').value.trim();
    const date = document.getElementById('meetingDate').value;
    const meetingType = document.querySelector('input[name="meetingType"]:checked').value;
    const location = document.getElementById('meetingLocation').value.trim();
    const capacity = document.getElementById('meetingCapacity').value;
    const description = document.getElementById('meetingDescription').value.trim();*/

    @Override
    public Meeting create(Long studyId, MeetingDTO meetingDTO, Members createdBy) {


        Study study = studyRepository.findById(studyId).orElseThrow(() -> new RuntimeException("Study Not Found"));

        Meeting meeting = Meeting.builder()
                .study(study)
                .title(meetingDTO.getTitle())
                .date(meetingDTO.getDate())
                .meetingType(Meeting.MeetingType.valueOf(meetingDTO.getMeetingType()))
                .location(meetingDTO.getLocation())
                .description(meetingDTO.getDescription())
                .status(Meeting.MeetingStatus.RECRUITING)
                .enabled(true)
                .createdBy(createdBy)
                .capacity(meetingDTO.getCapacity())
                .build();


        return meetingRepository.save(meeting);


    }
}
