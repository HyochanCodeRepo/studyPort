package com.example.studyport.repository;


import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    public List<Study> findByMembers_Id(Long memberId);


}
