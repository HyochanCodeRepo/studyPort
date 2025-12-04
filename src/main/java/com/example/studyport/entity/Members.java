package com.example.studyport.entity;

import com.example.studyport.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;

    private String phone;
    private String password;
    private String address;

    // 다중 관심사 (최대 3개, 카테고리 다대다 관계)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "member_categories",
        joinColumns = @JoinColumn(name = "member_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role;


    private String provider;
    private String providerId;
}
