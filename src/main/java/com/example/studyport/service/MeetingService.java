package com.example.studyport.service;

import com.example.studyport.dto.MeetingDTO;
import com.example.studyport.entity.Meeting;
import com.example.studyport.entity.Members;
import com.example.studyport.repository.MeetingRepository;

public interface MeetingService {

    public Meeting create(Long studyId, MeetingDTO meetingDTO, Members createdBy);

}
