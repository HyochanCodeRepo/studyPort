package com.example.studyport.repository;

import com.example.studyport.entity.StudyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {
    
    // 특정 스터디의 승인 요청 목록 (스터디장이 보는 용도) - N+1 쿼리 최적화
    @Query("SELECT sp FROM StudyParticipant sp JOIN FETCH sp.member JOIN FETCH sp.study WHERE sp.study.id = :studyId AND sp.status = :status")
    List<StudyParticipant> findByStudyIdAndStatus(@Param("studyId") Long studyId, @Param("status") StudyParticipant.ParticipantStatus status);
    
    // 내가 만든 스터디들의 승인 대기 건수 (뱃지용)
    @Query("SELECT COUNT(sp) FROM StudyParticipant sp WHERE sp.study.members.id = :authorId AND sp.status = :status")
    long countPendingRequestsByAuthor(@Param("authorId") Long authorId, @Param("status") StudyParticipant.ParticipantStatus status);
    
    // 내가 만든 모든 스터디의 승인 요청 목록 - N+1 쿼리 최적화
    @Query("SELECT sp FROM StudyParticipant sp JOIN FETCH sp.member JOIN FETCH sp.study WHERE sp.study.members.id = :authorId AND sp.status = :status ORDER BY sp.createdAt DESC")
    List<StudyParticipant> findPendingRequestsByAuthor(@Param("authorId") Long authorId, @Param("status") StudyParticipant.ParticipantStatus status);
    
    // 중복 신청 체크용
    Optional<StudyParticipant> findByStudyIdAndMemberId(Long studyId, Long memberId);
    
    // 특정 멤버가 특정 스터디에 승인된 상태인지 확인
    boolean existsByStudyIdAndMemberIdAndStatus(Long studyId, Long memberId, StudyParticipant.ParticipantStatus status);
    
    // 참여 중인 스터디 목록 조회 (APPROVED 상태) - N+1 쿼리 최적화
    @Query("SELECT sp FROM StudyParticipant sp JOIN FETCH sp.study WHERE sp.member.id = :memberId AND sp.status = :status")
    List<StudyParticipant> findByMember_IdAndStatus(@Param("memberId") Long memberId, @Param("status") String status);
}