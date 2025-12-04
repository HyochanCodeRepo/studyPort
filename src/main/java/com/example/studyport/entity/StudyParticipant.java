package com.example.studyport.entity;

import com.example.studyport.constant.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_participant")
@Data
@EntityListeners(AuditingEntityListener.class)
public class StudyParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Members member;
    
    @Column(columnDefinition = "TEXT")
    private String message; // 자기소개글
    
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status = ParticipantStatus.PENDING;
    
    /**
     * 스터디 내 역할
     * - USER: 일반 멤버
     * - STUDY_OPERATOR: 스터디 운영진
     * 기본값: USER
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'USER'")
    private Role role = Role.USER;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // 유니크 제약조건을 위한 설정
    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"study_id", "member_id"})
    })
    public static class UniqueConstraintDefinition {}
    
    public enum ParticipantStatus {
        PENDING,   // 승인 대기
        APPROVED,  // 승인됨
        REJECTED   // 거절됨
    }
}