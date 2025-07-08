package com.example.studyport.repository;

import com.example.studyport.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    List<StudyGroup> findByOwnerUno(Long uno);
}
