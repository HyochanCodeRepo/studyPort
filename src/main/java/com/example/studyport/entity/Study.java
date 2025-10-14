package com.example.studyport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    private String name;
    private String topic; // 카테고리

    private String description;

    private String location; // 지역

    private Integer capacity; // 최대인원

    private String password;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPrivate = false;

    // 추가된 필드들
    private String levelTag; // 난이도 (BEGINNER, INTERMEDIATE, ADVANCED)
    
    private String studyType; // 진행방식 (offline, online, hybrid)
    
    private String duration; // 스터디 기간
    
    private String frequency; // 모임 빈도
    
    private String goal; // 학습목표

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY)
    private List<Image> imageList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Members members;

    private String leader; // 스터디장 이름 (성능 향상을 위해 직접 저장)

    public Study setMembers(Members members) {
        this.members = members;
        return this;
    }
}
