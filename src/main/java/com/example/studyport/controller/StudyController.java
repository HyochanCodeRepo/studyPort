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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
        log.info("스터디 생성 페이지 진입");
        
        // 카테고리 목록을 모델에 추가
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        
        // 빈 StudyDTO 객체 추가
        model.addAttribute("studyDTO", new StudyDTO());

        return "study/create";
    }

    @GetMapping("/create/test")
    public String createTest() {
        log.info("스터디 생성 테스트 페이지 진입");
        return "study/test";
    }

    @PostMapping("/create/test")
    public String createTest(StudyDTO studyDTO, Principal principal, RedirectAttributes redirectAttributes) {
        log.info("=== 스터디 생성 테스트 (이미지 없이) ===");
        log.info("StudyDTO: {}", studyDTO);
        
        try {
            String email = principal.getName();
            Members members = memberRepository.findByEmail(email);
            
            if (members == null) {
                log.error("사용자를 찾을 수 없습니다: {}", email);
                redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
                return "redirect:/study/create/test";
            }
            
            MembersDTO membersDTO = modelMapper.map(members, MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);
            
            // 이미지 없이 스터디만 생성
            studyService.create(studyDTO, null);
            
            log.info("스터디 생성 성공 (이미지 없이)");
            redirectAttributes.addFlashAttribute("successMessage", "스터디가 성공적으로 생성되었습니다!");
            
        } catch (Exception e) {
            log.error("스터디 생성 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "스터디 생성 중 오류가 발생했습니다.");
            return "redirect:/study/create/test";
        }
        
        return "redirect:/";
    }

    @PostMapping("/upload-image")
    public String uploadImageTest(@RequestParam("imageFile") MultipartFile imageFile, 
                                 RedirectAttributes redirectAttributes) {
        log.info("=== 이미지 업로드 테스트 ===");
        log.info("업로드 파일: {}", imageFile.getOriginalFilename());
        log.info("파일 크기: {} bytes", imageFile.getSize());
        
        if (imageFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "파일을 선택해주세요.");
            return "redirect:/study/create/test";
        }
        
        try {
            // 간단한 파일 저장 테스트
            String uploadDir = "C:/ex/";
            String originalFilename = imageFile.getOriginalFilename();
            String newFilename = System.currentTimeMillis() + "_" + originalFilename;
            String filePath = uploadDir + newFilename;
            
            // 디렉터리 생성
            java.io.File directory = new java.io.File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
                log.info("디렉터리 생성: {}", uploadDir);
            }
            
            // 파일 저장
            java.io.File destFile = new java.io.File(filePath);
            imageFile.transferTo(destFile);
            
            log.info("파일 저장 성공: {}", filePath);
            redirectAttributes.addFlashAttribute("successMessage", 
                "이미지 업로드 성공: " + newFilename);
            
        } catch (Exception e) {
            log.error("이미지 업로드 실패", e);
            redirectAttributes.addFlashAttribute("errorMessage", "이미지 업로드 실패: " + e.getMessage());
        }
        
        return "redirect:/study/create/test";
    }

    @PostMapping("/create")
    public String create(StudyDTO studyDTO, Principal principal, 
                        RedirectAttributes redirectAttributes, Model model) {
        
        log.info("스터디 생성 요청 진입 (이미지 없음)");
        log.info("Principal: {}", principal);
        log.info("StudyDTO: {}", studyDTO);
        
        try {
            String email = principal.getName();
            log.info("로그인한 사용자 이메일: {}", email);
            
            Members members = memberRepository.findByEmail(email);
            if (members == null) {
                log.error("사용자를 찾을 수 없습니다: {}", email);
                model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
                return "study/create";
            }
            
            MembersDTO membersDTO = modelMapper.map(members, MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);
            
            // 필수 필드 검증
            if (studyDTO.getName() == null || studyDTO.getName().trim().isEmpty()) {
                model.addAttribute("errorMessage", "스터디명을 입력해주세요.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }
            
            if (studyDTO.getTopic() == null || studyDTO.getTopic().trim().isEmpty()) {
                model.addAttribute("errorMessage", "카테고리를 선택해주세요.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }
            
            if (studyDTO.getDescription() == null || studyDTO.getDescription().trim().isEmpty()) {
                model.addAttribute("errorMessage", "스터디 소개를 입력해주세요.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }
            
            if (studyDTO.getCapacity() == null || studyDTO.getCapacity().trim().isEmpty()) {
                model.addAttribute("errorMessage", "최대 인원을 입력해주세요.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }
            
            // 비공개 스터디의 경우 비밀번호 검증
            if (studyDTO.isPrivate() && (studyDTO.getPassword() == null || studyDTO.getPassword().trim().length() < 4)) {
                model.addAttribute("errorMessage", "비공개 스터디는 4자리 이상의 비밀번호가 필요합니다.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }
            
            // 스터디 생성 (이미지 없이)
            studyService.create(studyDTO, null);
            
            log.info("스터디 생성 성공 (이미지 없음)");
            redirectAttributes.addFlashAttribute("successMessage", "스터디가 성공적으로 생성되었습니다!");
            
        } catch (Exception e) {
            log.error("스터디 생성 중 오류 발생", e);
            model.addAttribute("errorMessage", "스터디 생성 중 오류가 발생했습니다. 다시 시도해주세요.");
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            return "study/create";
        }

        return "redirect:/";
    }

    @GetMapping("/list")
    public String list() {
        log.info("스터디 목록 페이지 진입");
        return "study/list";
    }

    @GetMapping("/view")
    public String view(Principal principal, Model model) {
        log.info("내 스터디 보기 페이지 진입");
        
        String email = principal.getName();
        Members members = memberRepository.findByEmail(email);
        
        if (members != null) {
            List<Study> studyList = studyRepository.findByMembers_Id(members.getId());
            List<StudyDTO> studyDTOList = studyList.stream()
                .map(study -> modelMapper.map(study, StudyDTO.class))
                .collect(Collectors.toList());
            
            model.addAttribute("studyDTOList", studyDTOList);
        }
        
        return "study/view";
    }
}
