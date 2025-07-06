package com.example.studyport.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDTO {

    private Long uno;

    private String userEmail;      // 이메일(아이디)

    private String userPassword;   // 패스워드(암호화 저장 권장)

    private String userAddress;    // 주소

    private String userBirth;   // 생년월일
}
