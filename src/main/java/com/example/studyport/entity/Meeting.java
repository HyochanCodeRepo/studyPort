package com.example.studyport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "meeting")
public class Meeting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(length = 255)
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingType meetingType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;
    
    @Column(nullable = false)
    private Boolean enabled;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Members createdBy;
    
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<MeetingVoter> voters = new ArrayList<>();
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Enum 정의
    public enum MeetingType {
        OFFLINE, ONLINE
    }
    
    public enum MeetingStatus {
        RECRUITING, CLOSED, DONE
    }
}
