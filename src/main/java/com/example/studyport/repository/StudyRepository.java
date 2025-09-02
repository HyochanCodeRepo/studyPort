package com.example.studyport.repository;

import com.example.studyport.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findByMembers_Id(Long memberId);
    
    // 최신 스터디 10개 조회 (ID 내림차순)
    List<Study> findTop10ByOrderByIdDesc();
    
    // 카테고리별 스터디 조회
    List<Study> findByTopicContaining(String topic);
    
    // 공개 스터디만 조회
    List<Study> findByIsPrivateFalse();
}
