package com.example.studyport.repository;

import com.example.studyport.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findByMembers_Id(Long memberId);

    // 최신 스터디 10개 조회 (ID 내림차순)
    List<Study> findTop10ByOrderByIdDesc();

    // 카테고리별 스터디 조회
    List<Study> findByTopicContaining(String topic);

    // 공개 스터디만 조회
    List<Study> findByIsPrivateFalse();

    // 스터디명 또는 지역으로 검색 (대소문자 무시)
    @Query("SELECT s FROM Study s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Study> searchByKeyword(@Param("keyword") String keyword);
}
