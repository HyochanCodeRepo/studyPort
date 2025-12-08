package com.example.studyport.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MeetingDTO {
    private Long id;
    private String title;
    private LocalDateTime date;
    private String meetingType;
    private String location;
    private Integer capacity;
    private String description;


}
