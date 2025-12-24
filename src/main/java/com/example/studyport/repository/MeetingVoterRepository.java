package com.example.studyport.repository;

import com.example.studyport.entity.MeetingVoter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingVoterRepository extends JpaRepository<MeetingVoter, Long> {
    
    /**
     * 특정 모임과 멤버로 참석자 조회
     */
    Optional<MeetingVoter> findByMeetingIdAndMemberId(Long meetingId, Long memberId);
    
    /**
     * 특정 모임의 모든 참석자 조회
     */
    List<MeetingVoter> findByMeetingId(Long meetingId);
    
    /**
     * 특정 모임의 참석자 수
     */
    Long countByMeetingId(Long meetingId);
}
