package com.example.studyport.entity;


import com.example.studyport.constant.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Members {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id" , nullable = false)
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;

    private String phone;
    private String password;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;
    private String providerId;
}
