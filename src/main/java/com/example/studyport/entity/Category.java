package com.example.studyport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 다단계 확장: 자기참조 parent
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    /*
    * INSERT INTO category (name) VALUES
                                ('IT/개발'),
                                ('외국어'),
                                ('자격증'),
                                ('문학/책'),
                                ('음악/예술'),
                                ('경영/경제'),
                                ('수학/과학'),
                                ('취업/면접'),
                                ('자기계발'),
                                ('취미/여가'),
                                ('시사/트렌드'),
                                ('운동/건강');
    *
    *
    *
    * */


}
