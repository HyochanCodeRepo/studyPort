package com.example.studyport.repository;

import com.example.studyport.entity.Meeting;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // study_id로 조회
    List<Meeting> findByStudy_Id(Long studyId, Sort sort);

    // study_id로 조회해서 enabled인 것만
    List<Meeting> findByStudy_IdAndEnabled(Long studyId, Boolean enabled, Sort sort);

    // study_id와 enabled로 조회 (날짜 오름차순)
    List<Meeting> findByStudyIdAndEnabledOrderByDateAsc(Long studyId, Boolean enabled);
}
