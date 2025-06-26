package com.example.studyport.repository;


import com.example.studyport.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {


}
