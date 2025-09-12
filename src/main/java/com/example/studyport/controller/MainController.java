package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Category;
import com.example.studyport.entity.Study;
import com.example.studyport.repository.CategoryRepository;
import com.example.studyport.repository.StudyRepository;
import com.example.studyport.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final MemberService memberService;

    @GetMapping("/")
    public String main(Principal principal, Model model, HttpSession session) {
        String email = "";
        String userName = "";
        boolean isLoggedIn = false;
        
        // 세션에서 로그인된 사용자 정보 확인
        String sessionEmail = (String) session.getAttribute("userEmail");
        
        if (sessionEmail != null) {
            email = sessionEmail;
            // 서비스에서 userName 가져오기
            userName = memberService.getUserNameByEmail(sessionEmail);
            isLoggedIn = true;
            log.info("세션에서 로그인된 사용자 확인: " + email + ", 실제 이름: " + userName);
        } else {
            // 기존 OAuth 또는 Principal 처리
            MembersDTO user = (MembersDTO) session.getAttribute("user");
            if (user != null) {
                log.info(user.getEmail());
                email = user.getEmail();
                userName = user.getName() != null ? user.getName() : email;
                isLoggedIn = true;
            } else if (principal != null) {
                email = principal.getName();
                userName = memberService.getUserNameByEmail(email);
                isLoggedIn = true;
            }
        }

        log.info("email: " + email + ", userName: " + userName + ", isLoggedIn: " + isLoggedIn);
        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", isLoggedIn);

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

        // 카테고리 목록 조회 (DB에서)
        List<Category> categories = categoryRepository.findAll();
        
        // 전체 스터디 목록과 topic별 그룹, 지역 목록, 카테고리 목록 전달
        model.addAttribute("studies", studyDTOList);
        model.addAttribute("studiesByTopic", studiesByTopic);
        model.addAttribute("topicList", studiesByTopic.keySet());
        model.addAttribute("locationList", locationSet);
        model.addAttribute("categories", categories);

        log.info("카테고리별 스터디 분류: {}", studiesByTopic.keySet());
        log.info("지역 목록: {}", locationSet);
        log.info("DB 카테고리 목록: {}", categories);

        return "main";
    }
    
    @GetMapping("/categories")
    public String categoryPage(@RequestParam("name") String categoryName, Principal principal, Model model, HttpSession session) {
        try {
            log.info("카테고리 페이지 요청: {}", categoryName);
            
            String email = "";
            String userName = "";
            boolean isLoggedIn = false;
            
            // 세션에서 로그인된 사용자 정보 확인 (메인과 동일한 로직)
            String sessionEmail = (String) session.getAttribute("userEmail");
            
            if (sessionEmail != null) {
                email = sessionEmail;
                userName = memberService.getUserNameByEmail(sessionEmail);
                isLoggedIn = true;
                log.info("세션에서 로그인된 사용자 확인: " + email + ", 실제 이름: " + userName);
            } else {
                MembersDTO user = (MembersDTO) session.getAttribute("user");
                if (user != null) {
                    log.info(user.getEmail());
                    email = user.getEmail();
                    userName = user.getName() != null ? user.getName() : email;
                    isLoggedIn = true;
                } else if (principal != null) {
                    email = principal.getName();
                    userName = memberService.getUserNameByEmail(email);
                    isLoggedIn = true;
                }
            }

            log.info("email: " + email + ", userName: " + userName + ", isLoggedIn: " + isLoggedIn);
            model.addAttribute("email", email);
            model.addAttribute("userName", userName);
            model.addAttribute("isLoggedIn", isLoggedIn);

            // 카테고리가 실제로 존재하는지 확인
            List<Category> categories = categoryRepository.findAll();
            boolean categoryExists = categories.stream()
                    .anyMatch(category -> categoryName.equals(category.getName()));
            
            if (!categoryExists) {
                log.warn("존재하지 않는 카테고리 요청: {}", categoryName);
                return "redirect:/";
            }

            // 해당 카테고리의 스터디만 조회 (공개 스터디만)
            List<Study> allStudies = studyRepository.findAll();
            List<StudyDTO> studyDTOList = allStudies.stream()
                    .filter(study -> study.getIsPrivate() == null || !study.getIsPrivate()) // 공개 스터디만
                    .filter(study -> categoryName.equals(study.getTopic())) // 해당 카테고리만
                    .map(study -> modelMapper.map(study, StudyDTO.class))
                    .collect(Collectors.toList());

            log.info("카테고리 '{}' 스터디 개수: {}", categoryName, studyDTOList.size());

            // 해당 카테고리의 지역 목록 추출
            Set<String> locationSet = studyDTOList.stream()
                    .map(StudyDTO::getLocation)
                    .filter(location -> location != null && !location.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            // 데이터 전달
            model.addAttribute("studies", studyDTOList);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("locationList", locationSet);
            model.addAttribute("categories", categories);

            log.info("카테고리 '{}' 지역 목록: {}", categoryName, locationSet);

            return "category";
            
        } catch (Exception e) {
            log.error("카테고리 페이지 처리 중 오류: {}", e.getMessage(), e);
            return "redirect:/";
        }
    }
}