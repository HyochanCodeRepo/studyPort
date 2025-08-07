package com.example.studyport.repository;

import com.example.studyport.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Members, Long> {


    public Members findByEmail(String email);

    boolean existsByEmail(String email);
}
