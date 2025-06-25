package com.example.studyport.dto;

import com.example.studyport.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MembersDTO {

    private Long id;

    private String name;
    private String email;

    private String phone;
    private String password;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;
}
