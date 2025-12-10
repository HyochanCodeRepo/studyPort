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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MeetingServiceImpl implements MeetingService {

    private final ModelMapper modelMapper = new ModelMapper();
    private final MeetingRepository meetingRepository;
    private final StudyRepository studyRepository;

    @Override
    public Meeting create(Long studyId, MeetingDTO meetingDTO, Members createdBy) {
        log.info("모임 생성 시작 - studyId: {}, title: {}", studyId, meetingDTO.getTitle());

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("Study Not Found"));

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

        Meeting savedMeeting = meetingRepository.save(meeting);
        log.info("모임 생성 완료 - meetingId: {}", savedMeeting.getId());

        return savedMeeting;
    }

    @Override
    public List<Meeting> getMeetingsByStudyId(Long studyId) {
        log.info("모임 목록 조회 (정렬 없음) - studyId: {}", studyId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("Study Not Found"));

        List<Meeting> meetings = meetingRepository.findByStudy_IdAndEnabled(
                studyId,
                true,
                Sort.by(Sort.Direction.ASC, "date")
        );

        log.info("조회된 모임 개수: {}", meetings.size());
        return meetings;
    }

    @Override
    public List<Meeting> getMeetingsByStudyId(Long studyId, Long currentUserId) {
        log.info("모임 목록 조회 (정렬 포함) - studyId: {}, currentUserId: {}", studyId, currentUserId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("Study Not Found"));

        List<Meeting> meetings = meetingRepository.findByStudy_IdAndEnabled(
                studyId,
                true,
                Sort.by(Sort.Direction.ASC, "date")
        );

        // currentUserId가 null이 아니면 정렬
        if (currentUserId != null) {
            meetings.sort((m1, m2) -> {
                boolean isM1Mine = m1.getCreatedBy().getId().equals(currentUserId);
                boolean isM2Mine = m2.getCreatedBy().getId().equals(currentUserId);
                
                // 내 모임이 먼저 오도록
                if (isM1Mine && !isM2Mine) return -1;
                if (!isM1Mine && isM2Mine) return 1;
                // 같은 카테고리면 날짜 순
                return m1.getDate().compareTo(m2.getDate());
            });
        }

        log.info("조회된 모임 개수: {}", meetings.size());
        return meetings;
    }

    @Override
    public Meeting updateMeeting(Long meetingId, MeetingDTO meetingDTO, Members member) {
        log.info("==========================================");
        log.info("모임 수정 요청");
        log.info("meetingId={}, title={}, memberId={}", meetingId, meetingDTO.getTitle(), member.getId());
        log.info("==========================================");

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting Not Found"));

        log.info("조회된 meeting: {}", meeting);
        log.info("현재 사용자: {}, 모임 작성자: {}", member.getId(), meeting.getCreatedBy().getId());

        // 현재 사용자가 작성자인지 확인 (권한 체크)
        if (!meeting.getCreatedBy().getId().equals(member.getId())) {
            log.error("권한 없음: 모임을 만든 사람만 수정할 수 있습니다. userId={}, createdById={}", 
                    member.getId(), meeting.getCreatedBy().getId());
            throw new IllegalArgumentException("자신이 만든 모임만 수정할 수 있습니다.");
        }

        // 모임 정보 업데이트
        meeting.setTitle(meetingDTO.getTitle());
        meeting.setDate(meetingDTO.getDate());
        meeting.setMeetingType(Meeting.MeetingType.valueOf(meetingDTO.getMeetingType()));
        meeting.setLocation(meetingDTO.getLocation());
        meeting.setCapacity(meetingDTO.getCapacity());
        meeting.setDescription(meetingDTO.getDescription());

        Meeting updatedMeeting = meetingRepository.save(meeting);

        log.info("==========================================");
        log.info("모임 수정 완료: meetingId={}, title={}", updatedMeeting.getId(), updatedMeeting.getTitle());
        log.info("==========================================");

        return updatedMeeting;
    }

    @Override
    public void deleteMeeting(Long meetingId, Members member) {
        log.info("모임 삭제 시작 - meetingId: {}, memberId: {}", meetingId, member.getId());
        
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        
        // 권한 검증: 자신이 만든 모임만 삭제 가능
        if (!meeting.getCreatedBy().getId().equals(member.getId())) {
            log.warn("삭제 권한 없음 - createdBy: {}, memberId: {}", 
                    meeting.getCreatedBy().getId(), member.getId());
            throw new IllegalArgumentException("자신이 만든 모임만 삭제할 수 있습니다.");
        }
        
        // enabled를 false로 설정 (soft delete)
        meeting.setEnabled(false);
        meetingRepository.save(meeting);
        
        log.info("모임 삭제 완료 - meetingId: {}", meetingId);
    }
}