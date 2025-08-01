package com.example.studyport.repository;

import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
//    public List<Image> findByStudyDTO(StudyDTO studyDTO);

    public Image findByStudy_IdAndRepImgYn(Long study_id, String repImgYn);

}
