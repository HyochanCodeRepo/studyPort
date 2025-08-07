package com.example.studyport.dto;

import com.example.studyport.constant.Role;
import com.example.studyport.entity.Members;
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

    private Long categoryId; // <- 관심사

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;
    private String providerId;

    public MembersDTO(Members members){
        this.id = members.getId();
        this.name = members.getName();
        this.email = members.getEmail();
        this.phone = members.getPhone();
        this.password = members.getPassword();
        this.address = members.getAddress();
        this.provider = members.getProvider();
        this.providerId = members.getProviderId();
        this.role = members.getRole();
    }
}
