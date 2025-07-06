package com.example.studyport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uno;

    @Column(unique = true, nullable = false)
    private String userEmail;      // 이메일(아이디)

    @Column(nullable = false)
    private String userPassword;   // 패스워드(암호화 저장 권장)

    private String userAddress;    // 주소
    private LocalDate userBirth;   // 생년월일
}
