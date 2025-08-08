package com.example.studyport.controller;

import com.example.studyport.dto.CategoryDTO;
import com.example.studyport.dto.MembersDTO;
import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Category;
import com.example.studyport.entity.Members;
import com.example.studyport.entity.Study;
import com.example.studyport.repository.CategoryRepository;
import com.example.studyport.repository.MemberRepository;
import com.example.studyport.repository.StudyRepository;
import com.example.studyport.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.security.Principal;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyController {
    private final StudyService studyService;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final StudyRepository studyRepository;

    @GetMapping("/create")

    public String create(Model model) {
        List<Category> categories =
                categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "study/create";
    }

    @PostMapping("/create")
    public String create(StudyDTO studyDTO, MultipartFile mainimg, Principal principal, Model model) throws IOException {

        log.info("Study create 진입");
        log.info("Study create 진입");
        String email =
                principal.getName();
        log.info(email);
        log.info(principal.getName());

        log.info(mainimg.getOriginalFilename());
        log.info(studyDTO);

        Members members =
            memberRepository.findByEmail(email);
        MembersDTO membersDTO =
            modelMapper.map(members, MembersDTO.class);
        studyDTO.setMembersDTO(membersDTO);
        studyService.create(studyDTO, mainimg);


        return "redirect:/";
    }

    @GetMapping("/list")
    public String list() {
        return "study/list";
    }

    @GetMapping("/view")
    public String view(Principal principal, Model model) {
        String email = principal.getName();
        Members members =
            memberRepository.findByEmail(email);
        List<Study> studyList =
                studyRepository.findByMembers_Id(members.getId());
        model.addAttribute("studyList", studyList);
        return "study/view";
    }
}
