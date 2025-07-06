package com.example.studyport.repository;

import com.example.studyport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //DB 에서 유저의 email을 가져 오느메서드
    Optional<User> findByUserEmail(String userEmail);

}
