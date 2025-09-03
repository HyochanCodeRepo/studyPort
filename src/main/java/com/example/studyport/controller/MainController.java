package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Study;
import com.example.studyport.repository.StudyRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/")
    public String main(Principal principal, Model model, HttpSession session) {
        String email = "";
        MembersDTO user = (MembersDTO) session.getAttribute("user");
        if (user != null) {
            log.info(user.getEmail());
            email = user.getEmail();
        } else if (principal != null) {
            email = principal.getName();
        }

        log.info("email: " + email);
        log.info("principal: " + principal);
        model.addAttribute("email", email);

        // 최신 스터디 10개 조회 (공개 스터디만)
        List<Study> studyList = studyRepository.findTop10ByOrderByIdDesc();
        List<StudyDTO> studyDTOList = studyList.stream()
                .filter(study -> study.getIsPrivate() == null || !study.getIsPrivate()) // null 체크 추가
                .map(study -> modelMapper.map(study, StudyDTO.class))
                .collect(Collectors.toList());

        log.info("조회된 공개 스터디 개수: {}", studyDTOList.size());

        // topic별로 그룹화
        Map<String, List<StudyDTO>> studiesByTopic = studyDTOList.stream()
                .collect(Collectors.groupingBy(
                        study -> study.getTopic() != null ? study.getTopic() : "기타",
                        LinkedHashMap::new,  // 순서 유지
                        Collectors.toList()
                ));

        // 지역 목록 추출 (null이 아닌 것만)
        Set<String> locationSet = studyDTOList.stream()
                .map(StudyDTO::getLocation)
                .filter(location -> location != null && !location.trim().isEmpty())
                .collect(Collectors.toSet());

        // 전체 스터디 목록과 topic별 그룹, 지역 목록 전달
        model.addAttribute("studies", studyDTOList);
        model.addAttribute("studiesByTopic", studiesByTopic);
        model.addAttribute("topicList", studiesByTopic.keySet());
        model.addAttribute("locationList", locationSet);

        log.info("카테고리별 스터디 분류: {}", studiesByTopic.keySet());
        log.info("지역 목록: {}", locationSet);

        return "main";
    }
}