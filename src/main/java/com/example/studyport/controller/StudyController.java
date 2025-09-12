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
import com.example.studyport.repository.StudyParticipantRepository;
import com.example.studyport.entity.StudyParticipant;
import com.example.studyport.service.MemberService;
import com.example.studyport.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import jakarta.servlet.http.HttpSession;
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
    private final StudyParticipantRepository studyParticipantRepository;
    private final MemberService memberService;

    @GetMapping("/create")
    public String create(Model model, Principal principal) {
        log.info("스터디 생성 페이지 진입");
        
        // 로그인 체크
        if (principal == null) {
            log.info("로그인되지 않은 사용자의 스터디 생성 페이지 접근 시도");
            return "redirect:/members/login";
        }
        
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
            if (Boolean.TRUE.equals(studyDTO.getIsPrivate()) && (studyDTO.getPassword() == null || studyDTO.getPassword().trim().length() < 4)) {
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

    @GetMapping("/read/{id}")
    public String read(@PathVariable Long id, Model model, Principal principal, HttpSession session) {
        log.info("스터디 상세 페이지 진입: {}", id);
        
        // 로그인 체크
        if (principal == null) {
            log.info("로그인되지 않은 사용자의 스터디 상세 페이지 접근 시도");
            return "redirect:/members/login";
        }
        
        Study study = studyRepository.findById(id).orElse(null);
        if (study == null) {
            log.error("스터디를 찾을 수 없습니다: {}", id);
            return "redirect:/";
        }
        
        log.info("조회된 Study 데이터: {}", study);
        
        StudyDTO studyDTO = modelMapper.map(study, StudyDTO.class);
        
        log.info("매핑된 StudyDTO 데이터: {}", studyDTO);
        
        // MembersDTO 수동 설정
        if (study.getMembers() != null) {
            MembersDTO membersDTO = modelMapper.map(study.getMembers(), MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);
            log.info("매핑된 MembersDTO 데이터: {}", membersDTO);
        }
        
        // 로그인한 사용자 정보 추가 (메인 페이지와 동일한 로직)
        String email = "";
        String userName = "";
        boolean isLoggedIn = false;
        
        // 세션에서 로그인된 사용자 정보 확인
        String sessionEmail = (String) session.getAttribute("userEmail");
        String sessionUserName = (String) session.getAttribute("userName");
        
        if (sessionEmail != null) {
            email = sessionEmail;
            userName = sessionUserName != null ? sessionUserName : email;
            isLoggedIn = true;
        } else if (principal != null) {
            email = principal.getName();
            userName = email;
            isLoggedIn = true;
        }
        
        // 현재 로그인한 사용자가 스터디장인지 확인
        Members currentUser = null;
        boolean isStudyAuthor = false;
        if (principal != null) {
            currentUser = memberRepository.findByEmail(principal.getName());
            if (currentUser != null && study.getMembers() != null) {
                isStudyAuthor = study.getMembers().getId().equals(currentUser.getId());
            }
        }
        
        // 스터디장인 경우에만 참여자 목록 조회
        List<StudyParticipant> pendingParticipants = null;
        List<StudyParticipant> approvedParticipants = null;
        
        if (isStudyAuthor) {
            // 승인 대기 중인 참여자
            pendingParticipants = studyParticipantRepository.findByStudyIdAndStatus(
                study.getId(), StudyParticipant.ParticipantStatus.PENDING);
            
            // 승인된 참여자
            approvedParticipants = studyParticipantRepository.findByStudyIdAndStatus(
                study.getId(), StudyParticipant.ParticipantStatus.APPROVED);
        }
        
        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("study", studyDTO);
        model.addAttribute("isStudyAuthor", isStudyAuthor);
        model.addAttribute("pendingParticipants", pendingParticipants);
        model.addAttribute("approvedParticipants", approvedParticipants);
        
        return "study/read";
    }

    @GetMapping("/manage")
    public String manage(Principal principal, Model model, HttpSession session) {
        log.info("내 스터디 관리 페이지 진입");
        
        // 로그인 체크
        if (principal == null) {
            log.info("로그인되지 않은 사용자의 스터디 관리 페이지 접근 시도");
            return "redirect:/members/login";
        }
        
        String email = principal.getName();
        Members currentUser = memberRepository.findByEmail(email);
        
        if (currentUser == null) {
            log.error("사용자를 찾을 수 없습니다: {}", email);
            return "redirect:/members/login";
        }
        
        // 내가 관리하는 스터디 (스터디장인 스터디)
        List<Study> managedStudies = studyRepository.findByMembers_Id(currentUser.getId());
        List<StudyDTO> managedStudyDTOList = managedStudies.stream()
            .map(study -> modelMapper.map(study, StudyDTO.class))
            .collect(Collectors.toList());
        
        // 승인 대기 중인 요청 건수 조회
        long pendingRequestsCount = studyParticipantRepository.countPendingRequestsByAuthor(
            currentUser.getId(), 
            StudyParticipant.ParticipantStatus.PENDING
        );
        
        // 헤더 표시용 사용자 정보 추가
        String userName = "";
        boolean isLoggedIn = false;
        
        // 세션에서 로그인된 사용자 정보 확인
        String sessionEmail = (String) session.getAttribute("userEmail");
        String sessionUserName = (String) session.getAttribute("userName");
        
        if (sessionEmail != null) {
            email = sessionEmail;
            userName = sessionUserName != null ? sessionUserName : email;
            isLoggedIn = true;
        } else if (principal != null) {
            email = principal.getName();
            userName = email;
            isLoggedIn = true;
        }
        
        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("managedStudies", managedStudyDTOList);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("pendingRequestsCount", pendingRequestsCount);
        
        return "study/manage";
    }
    
    /**
     * 스터디 참여 신청
     */
    @PostMapping("/join")
    public String joinStudy(@RequestParam Long studyId, 
                           @RequestParam String message, 
                           Principal principal, 
                           RedirectAttributes redirectAttributes) {
        
        log.info("스터디 참여 신청: studyId={}, message={}", studyId, message);
        
        try {
            // 로그인 체크
            if (principal == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
                return "redirect:/members/login";
            }
            
            String email = principal.getName();
            Members member = memberRepository.findByEmail(email);
            
            if (member == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
                return "redirect:/study/read/" + studyId;
            }
            
            // 스터디 조회
            Study study = studyRepository.findById(studyId).orElse(null);
            if (study == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "스터디를 찾을 수 없습니다.");
                return "redirect:/";
            }
            
            // 자신이 만든 스터디인지 확인
            if (study.getMembers().getId().equals(member.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "자신이 만든 스터디에는 참여 신청할 수 없습니다.");
                return "redirect:/study/read/" + studyId;
            }
            
            // 이미 신청했는지 확인
            var existingParticipant = studyParticipantRepository.findByStudyIdAndMemberId(studyId, member.getId());
            if (existingParticipant.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "이미 참여 신청한 스터디입니다.");
                return "redirect:/study/read/" + studyId;
            }
            
            // 참여 신청 생성
            StudyParticipant participant = new StudyParticipant();
            participant.setStudy(study);
            participant.setMember(member);
            participant.setMessage(message);
            participant.setStatus(StudyParticipant.ParticipantStatus.PENDING);
            
            studyParticipantRepository.save(participant);
            
            log.info("스터디 참여 신청 완료: studyId={}, memberId={}", studyId, member.getId());
            redirectAttributes.addFlashAttribute("successMessage", "스터디 참여 신청이 완료되었습니다. 스터디장의 승인을 기다려주세요.");
            
        } catch (Exception e) {
            log.error("스터디 참여 신청 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "참여 신청 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
        
        return "redirect:/study/read/" + studyId;
    }
    
    /**
     * 승인 요청 목록 페이지
     */
    @GetMapping("/approval-requests")
    public String approvalRequests(Principal principal, Model model, HttpSession session) {
        log.info("승인 요청 목록 페이지 진입");
        
        // 로그인 체크
        if (principal == null) {
            log.info("로그인되지 않은 사용자의 승인 요청 페이지 접근 시도");
            return "redirect:/members/login";
        }
        
        String email = principal.getName();
        Members currentUser = memberRepository.findByEmail(email);
        
        if (currentUser == null) {
            log.error("사용자를 찾을 수 없습니다: {}", email);
            return "redirect:/members/login";
        }
        
        // 내가 만든 스터디들의 승인 대기 요청 목록 조회
        List<StudyParticipant> pendingRequests = studyParticipantRepository.findPendingRequestsByAuthor(
            currentUser.getId(), 
            StudyParticipant.ParticipantStatus.PENDING
        );
        
        // 승인 대기 건수
        long pendingRequestsCount = pendingRequests.size();
        
        // 헤더 표시용 사용자 정보 추가
        String userName = memberService.getUserNameByEmail(email);
        boolean isLoggedIn = true;
        
        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("pendingRequestsCount", pendingRequestsCount);
        
        return "study/approval-requests";
    }
    
    /**
     * 승인 요청 승인/거절 처리
     */
    @PostMapping("/approval-requests/{participantId}")
    public String processApprovalRequest(@PathVariable Long participantId,
                                       @RequestParam String action,
                                       Principal principal,
                                       RedirectAttributes redirectAttributes) {
        
        log.info("승인 요청 처리: participantId={}, action={}", participantId, action);
        
        try {
            // 로그인 체크
            if (principal == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
                return "redirect:/members/login";
            }
            
            String email = principal.getName();
            Members currentUser = memberRepository.findByEmail(email);
            
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
                return "redirect:/study/approval-requests";
            }
            
            // 참여 신청 조회
            StudyParticipant participant = studyParticipantRepository.findById(participantId).orElse(null);
            if (participant == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "승인 요청을 찾을 수 없습니다.");
                return "redirect:/study/approval-requests";
            }
            
            // 권한 체크: 해당 스터디의 스터디장인지 확인
            if (!participant.getStudy().getMembers().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "해당 스터디의 승인 권한이 없습니다.");
                return "redirect:/study/approval-requests";
            }
            
            // 승인/거절 처리
            if ("approve".equals(action)) {
                participant.setStatus(StudyParticipant.ParticipantStatus.APPROVED);
                studyParticipantRepository.save(participant);
                
                log.info("승인 요청 승인 완료: participantId={}", participantId);
                redirectAttributes.addFlashAttribute("successMessage", 
                    participant.getMember().getName() + "님의 스터디 참여 신청을 승인했습니다.");
                    
            } else if ("reject".equals(action)) {
                participant.setStatus(StudyParticipant.ParticipantStatus.REJECTED);
                studyParticipantRepository.save(participant);
                
                log.info("승인 요청 거절 완료: participantId={}", participantId);
                redirectAttributes.addFlashAttribute("successMessage", 
                    participant.getMember().getName() + "님의 스터디 참여 신청을 거절했습니다.");
                    
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "잘못된 요청입니다.");
            }
            
        } catch (Exception e) {
            log.error("승인 요청 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "처리 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
        
        return "redirect:/study/approval-requests";
    }
    
    /**
     * 스터디장 전용 스터디 상세 관리 페이지
     */
    @GetMapping("/manage/{id}")
    public String manageStudyDetail(@PathVariable Long id, Principal principal, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        log.info("스터디장 전용 관리 상세페이지 진입: studyId={}", id);
        
        // 로그인 체크
        if (principal == null) {
            log.info("로그인되지 않은 사용자의 스터디 관리 페이지 접근 시도");
            return "redirect:/members/login";
        }
        
        String email = principal.getName();
        Members currentUser = memberRepository.findByEmail(email);
        
        if (currentUser == null) {
            log.error("사용자를 찾을 수 없습니다: {}", email);
            return "redirect:/members/login";
        }
        
        // 스터디 조회
        Study study = studyRepository.findById(id).orElse(null);
        if (study == null) {
            log.error("스터디를 찾을 수 없습니다: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 스터디입니다.");
            return "redirect:/study/manage";
        }
        
        // 권한 체크: 스터디장인지 확인
        if (!study.getMembers().getId().equals(currentUser.getId())) {
            log.warn("스터디장이 아닌 사용자의 관리 페이지 접근 시도: userId={}, studyId={}", currentUser.getId(), id);
            redirectAttributes.addFlashAttribute("errorMessage", "해당 스터디의 관리 권한이 없습니다.");
            return "redirect:/study/read/" + id;
        }
        
        log.info("조회된 Study 데이터: {}", study);
        
        StudyDTO studyDTO = modelMapper.map(study, StudyDTO.class);
        
        // MembersDTO 수동 설정
        if (study.getMembers() != null) {
            MembersDTO membersDTO = modelMapper.map(study.getMembers(), MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);
            log.info("매핑된 MembersDTO 데이터: {}", membersDTO);
        }
        
        // 승인 대기 중인 참여자
        List<StudyParticipant> pendingParticipants = studyParticipantRepository.findByStudyIdAndStatus(
            study.getId(), StudyParticipant.ParticipantStatus.PENDING);
        
        // 승인된 참여자
        List<StudyParticipant> approvedParticipants = studyParticipantRepository.findByStudyIdAndStatus(
            study.getId(), StudyParticipant.ParticipantStatus.APPROVED);
        
        // 헤더 표시용 사용자 정보 추가
        String userName = memberService.getUserNameByEmail(email);
        boolean isLoggedIn = true;
        
        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("study", studyDTO);
        model.addAttribute("isStudyAuthor", true); // 스터디장만 접근 가능하므로 true
        model.addAttribute("pendingParticipants", pendingParticipants);
        model.addAttribute("approvedParticipants", approvedParticipants);
        
        log.info("스터디장 관리 페이지 데이터 로드 완료: pendingCount={}, approvedCount={}", 
            pendingParticipants.size(), approvedParticipants.size());
        
        return "study/manage-detail";
    }
    
    /**
     * 스터디장 전용 스터디 상세 조회 페이지 (관리자 전용)
     */
    @GetMapping("/admin/read/{id}")
    public String adminRead(@PathVariable Long id, Model model, Principal principal, HttpSession session) {
        log.info("스터디장 전용 상세 페이지 진입: {}", id);
        
        // 로그인 체크
        if (principal == null) {
            log.info("로그인되지 않은 사용자의 스터디 상세 페이지 접근 시도");
            return "redirect:/members/login";
        }
        
        Study study = studyRepository.findById(id).orElse(null);
        if (study == null) {
            log.error("스터디를 찾을 수 없습니다: {}", id);
            return "redirect:/";
        }
        
        // 현재 로그인한 사용자가 스터디장인지 확인
        Members currentUser = memberRepository.findByEmail(principal.getName());
        if (currentUser == null || !study.getMembers().getId().equals(currentUser.getId())) {
            log.warn("스터디장이 아닌 사용자의 admin/read 접근 시도: {}", principal.getName());
            return "redirect:/study/read/" + id;
        }
        
        log.info("조회된 Study 데이터: {}", study);
        
        StudyDTO studyDTO = modelMapper.map(study, StudyDTO.class);
        
        log.info("매핑된 StudyDTO 데이터: {}", studyDTO);
        
        // MembersDTO 수동 설정
        if (study.getMembers() != null) {
            MembersDTO membersDTO = modelMapper.map(study.getMembers(), MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);
            log.info("매핑된 MembersDTO 데이터: {}", membersDTO);
        }
        
        // 로그인한 사용자 정보 추가
        String email = "";
        String userName = "";
        boolean isLoggedIn = false;
        
        // 세션에서 로그인된 사용자 정보 확인
        String sessionEmail = (String) session.getAttribute("userEmail");
        String sessionUserName = (String) session.getAttribute("userName");
        
        if (sessionEmail != null) {
            email = sessionEmail;
            userName = sessionUserName != null ? sessionUserName : email;
            isLoggedIn = true;
        } else if (principal != null) {
            email = principal.getName();
            userName = email;
            isLoggedIn = true;
        }
        
        // 승인 대기 중인 참여자 (스터디장만 볼 수 있음)
        List<StudyParticipant> pendingParticipants = studyParticipantRepository.findByStudyIdAndStatus(
            study.getId(), StudyParticipant.ParticipantStatus.PENDING);
        
        // 승인된 참여자
        List<StudyParticipant> approvedParticipants = studyParticipantRepository.findByStudyIdAndStatus(
            study.getId(), StudyParticipant.ParticipantStatus.APPROVED);
        
        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("study", studyDTO);
        model.addAttribute("isStudyAuthor", true); // 스터디장만 접근 가능하므로 true
        model.addAttribute("pendingParticipants", pendingParticipants);
        model.addAttribute("approvedParticipants", approvedParticipants);
        
        return "study/admin-read";
    }
}
