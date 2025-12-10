package com.example.studyport.service;

import com.example.studyport.dto.MeetingDTO;
import com.example.studyport.entity.Meeting;
import com.example.studyport.entity.Members;
import com.example.studyport.repository.MeetingRepository;

import java.util.List;

public interface MeetingService {

    public Meeting create(Long studyId, MeetingDTO meetingDTO, Members createdBy);

    public List<Meeting> getMeetingsByStudyId(Long studyId);

    public List<Meeting> getMeetingsByStudyId(Long studyId, Long currentUserId);

    public Meeting updateMeeting(Long meetingId, MeetingDTO meetingDTO, Members member);

    public void deleteMeeting(Long meetingId, Members member);

}
