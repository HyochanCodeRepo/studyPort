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
    private String topic;

    private String description;

    private String location;

    private String capacity;

    private String password;

    private Boolean isPrivate;


    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY)
    private List<Image> imageList;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Members members;

    public Study setMembers(Members members) {
        this.members = members;
        return this;
    }


}
