package com.example.studyport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// StudyGroup.java
/**
 * 스터디 그룹 엔티티
 *  - 사용자가 만든 학습 모임을 표현
 *  - 이후 GroupMember, SessionLog 등과 1:N 관계 예상
 */
@Entity
@Getter @Setter @NoArgsConstructor
public class StudyGroup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;           // PK: 그룹 ID

    // StudyGroup.java
    private String iconName;

    /* 그룹장(생성자) */
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;             // FK → User.uno

    /* ───────── 기본 정보 ───────── */
    private String title;           // 그룹 이름
    private String topic;           // 학습 주제
    private Integer capacity;       // 최대 인원
    private String levelTag;        // 난이도 태그 (BEGINNER/INTERMEDIATE/ADVANCED)
    private Boolean isPrivate;      // true = 비공개(초대링크 전용)

    @Column(columnDefinition = "TEXT")
    private String description;     // 그룹 설명 (Markdown 허용 예정)

    /* 🌟 리서치(주제) 파일 경로 */
    private String researchFilePath; // 업로드된 원본 파일 저장 위치 ("/uploads/..." 등)

    /* 상태 관리 */
    private String status = "OPEN"; // OPEN / CLOSED / ONGOING / END

    /* 생성·수정 시각 */
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    /* 헬퍼 메서드 --------------------------------------------------- */

    /** 그룹 정보 수정 시 타임스탬프 자동 업데이트 */
    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    /** 현재 인원 수가 꽉 찼는지 여부 (나중에 GroupMember count와 함께 사용) */
    public boolean isFull(int currentSize){
        return currentSize >= capacity;
    }
}
