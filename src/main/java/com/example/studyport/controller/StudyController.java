package com.example.studyport.controller;

import com.example.studyport.dto.CategoryDTO;
import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Category;
import com.example.studyport.repository.CategoryRepository;
import com.example.studyport.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyController {
    private final StudyService studyService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/create")
    public String create(Model model) {
        List<Category> categories =
            categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "study/create";
    }

    @PostMapping("/create")
    public String create(StudyDTO studyDTO) {

        log.info("Study create 진입");
        log.info("Study create 진입");
        log.info(studyDTO);


        return "study/create";
    }

    @GetMapping("/list")
    public String list() {
        return "study/list";
    }
}
