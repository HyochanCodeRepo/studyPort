package com.example.studyport.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor /*빌더 어노 테이션을 쓰기 위해서는 필요함*/
@Builder
public class StudyGroupDTO {

    private Long groupId;

    private String title;

    private String topic;

    private Integer capacity;

    private String levelTag;

    private Boolean isPrivate;

    private String description;

    private Long ownerId; // owner FK

    private String iconName;

    //파일 저장 경로
    private String researchFilePath; // ← 응답용(저장 후 경로 세팅)
}
