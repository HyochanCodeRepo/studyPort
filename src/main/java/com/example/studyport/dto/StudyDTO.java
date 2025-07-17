package com.example.studyport.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudyDTO {

    private Long id;

    private String name;

    private String description;

    private String location;

    private String capacity;

    private String leader;

    private MembersDTO membersDTO;
    private String password;

    public StudyDTO setMembersDTO(MembersDTO membersDTO) {
        this.membersDTO = membersDTO;
        return this;
    }

}
