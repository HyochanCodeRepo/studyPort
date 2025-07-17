package com.example.studyport.dto;

import com.example.studyport.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDTO {

    private Long uno;

    private String userName;

    private String userEmail;      // 이메일(아이디)

    private String userPassword;   // 패스워드(암호화 저장 권장)

    private String userAddress;    // 주소

    private String userBirth;   // 생년월일

    private String provider;        //공급자 (google, naver ...)
    private String providerId;      //공급 아이디 (OAuth 에서 제공)

    //엔티티 넣어서 dto 반환

    public UserDTO(User user) {
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.userPassword = user.getUserPassword();
        this.userAddress = user.getUserAddress();
        this.userBirth = user.getUserBirth() != null
                ? user.getUserBirth().toString()
                : ""; //todo LocalDate->String
        this.provider = user.getProvider();
        this.providerId = user.getProviderId();
        this.uno = user.getUno();
    }
}
