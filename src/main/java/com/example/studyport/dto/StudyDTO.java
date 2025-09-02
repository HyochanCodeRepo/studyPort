package com.example.studyport.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudyDTO {

    private Long id;

    private String name; // 스터디명

    private String description; // 스터디 소개

    private String location; // 지역

    private String capacity; // 최대인원

    private String topic; // 카테고리

    private boolean isPrivate; // 비공개 여부

    private String password; // 비밀번호

    // 추가된 필드들
    private String levelTag; // 난이도 (BEGINNER, INTERMEDIATE, ADVANCED)
    
    private String studyType; // 진행방식 (offline, online, hybrid)
    
    private String duration; // 스터디 기간
    
    private String frequency; // 모임 빈도
    
    private String goal; // 학습목표

    private MembersDTO membersDTO;

    private List<ImageDTO> imageDTOList;

    public StudyDTO setMembersDTO(MembersDTO membersDTO) {
        this.membersDTO = membersDTO;
        return this;
    }

    public StudyDTO setImageDTOList(List<ImageDTO> imageDTOList) {
        this.imageDTOList = imageDTOList;
        return this;
    }
}
