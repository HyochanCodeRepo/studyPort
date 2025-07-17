package com.example.studyport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uno;

    private String userName;        //유저 이름 추가했습니다...

    @Column(unique = true, nullable = false)
    private String userEmail;      // 이메일(아이디)

//    @Column(nullable = false)
    private String userPassword;   // 패스워드(암호화 저장 권장)

    private String userAddress;    // 주소
    private LocalDate userBirth;   // 생년월일

    private String provider;        //공급자 (google, naver ...)
    private String providerId;      //공급 아이디 (OAuth 에서 제공)
}
