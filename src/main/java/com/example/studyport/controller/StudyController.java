package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Category;
import com.example.studyport.entity.Members;
import com.example.studyport.entity.Study;
import com.example.studyport.entity.StudyParticipant;
import com.example.studyport.repository.CategoryRepository;
import com.example.studyport.repository.MemberRepository;
import com.example.studyport.repository.StudyParticipantRepository;
import com.example.studyport.repository.StudyRepository;
import com.example.studyport.service.MemberService;
import com.example.studyport.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/study")
@RequiredArgsConstructor
@Log4j2
public class StudyController {

    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final MemberService memberService;
    private final StudyParticipantRepository studyParticipantRepository;

    @GetMapping("/create")
    public String create(Model model, Principal principal) {
        log.info("===========================================");
        log.info("스터디 생성 페이지 진입");
        log.info("===========================================");

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        // 로그인 상태 확인
        if (principal != null) {
            model.addAttribute("isLoggedIn", true);
            String email = principal.getName();
            Members members = memberRepository.findByEmail(email);
            if (members != null) {
                model.addAttribute("userName", members.getName());
                model.addAttribute("email", email);
            }
        } else {
            return "redirect:/members/login";
//            model.addAttribute("isLoggedIn", false);

        }

        return "study/create";
    }

    @PostMapping("/create")
    public String create(StudyDTO studyDTO,
                         @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                         Principal principal,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        log.info("===========================================");
        log.info("스터디 생성 요청 진입");
        log.info("===========================================");
        log.info("Principal: {}", principal);
        log.info("StudyDTO: {}", studyDTO);
        log.info("썸네일 이미지: {}", thumbnail != null && !thumbnail.isEmpty() ? thumbnail.getOriginalFilename() : "없음");

        try {
            // 로그인 확인
            if (principal == null) {
                log.error("로그인되지 않은 사용자의 요청");
                model.addAttribute("errorMessage", "로그인이 필요합니다.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }

            String email = principal.getName();
            log.info("로그인한 사용자 이메일: {}", email);

            Members members = memberRepository.findByEmail(email);
            if (members == null) {
                log.error("사용자를 찾을 수 없습니다: {}", email);
                model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }

            com.example.studyport.dto.MembersDTO membersDTO = modelMapper.map(members, com.example.studyport.dto.MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);

            // 필수 필드 검증
            if (studyDTO.getName() == null || studyDTO.getName().trim().isEmpty()) {
                model.addAttribute("errorMessage", "스터디명을 입력해주세요.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }

            // 스터디명 길이 검증
            if (studyDTO.getName().length() > 100) {
                model.addAttribute("errorMessage", "스터디명은 100자 이내여야 합니다.");
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

            // 스터디 소개 길이 검증
            if (studyDTO.getDescription().length() > 1000) {
                model.addAttribute("errorMessage", "스터디 소개는 1000자 이내여야 합니다.");
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

            // 최대 인원 유효성 검증
            try {
                int capacity = Integer.parseInt(studyDTO.getCapacity());
                if (capacity < 2 || capacity > 100) {
                    model.addAttribute("errorMessage", "최대 인원은 2명 이상 100명 이하여야 합니다.");
                    List<Category> categories = categoryRepository.findAll();
                    model.addAttribute("categories", categories);
                    return "study/create";
                }
            } catch (NumberFormatException e) {
                model.addAttribute("errorMessage", "최대 인원은 숫자여야 합니다.");
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

            // 비밀번호 길이 검증
            if (Boolean.TRUE.equals(studyDTO.getIsPrivate()) && studyDTO.getPassword().length() > 20) {
                model.addAttribute("errorMessage", "비밀번호는 20자 이내여야 합니다.");
                List<Category> categories = categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "study/create";
            }

            log.info("===========================================");
            log.info("검증 완료, 스터디 생성 서비스 호출");
            log.info("===========================================");

            // 스터디 생성 (썸네일 이미지 포함)
            studyService.create(studyDTO, thumbnail);

            log.info("===========================================");
            log.info("스터디 생성 성공");
            log.info("===========================================");
            redirectAttributes.addFlashAttribute("successMessage", "스터디가 성공적으로 생성되었습니다!");

        } catch (Exception e) {
            log.error("===========================================");
            log.error("스터디 생성 중 오류 발생", e);
            log.error("===========================================");

            String errorMessage = "스터디 생성 중 오류가 발생했습니다. ";

            // 상세한 에러 메시지
            if (e.getCause() != null) {
                String causeMsg = e.getCause().getMessage();
                if (causeMsg != null && causeMsg.contains("FileCountLimitExceededException")) {
                    errorMessage = "⚠ 동시 업로드 파일 개수가 많습니다. 다시 시도해주세요.";
                } else if (causeMsg != null && causeMsg.contains("FileSizeLimitExceededException")) {
                    errorMessage = "⚠ 파일 크기가 너무 큽니다.";
                }
            }

            model.addAttribute("errorMessage", errorMessage);
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
    public String read(@PathVariable Long id, Model model, Principal principal) {
        log.info("스터디 상세 페이지 진입: {}", id);

        Study study = studyRepository.findById(id).orElse(null);
        if (study == null) {
            log.error("스터디를 찾을 수 없습니다: {}", id);
            return "redirect:/";
        }

        log.info("조회된 Study 데이터: {}", study);

        StudyDTO studyDTO = modelMapper.map(study, StudyDTO.class);

        log.info("매핑된 StudyDTO 데이터: {}", studyDTO);

        // MembersDTO 수동 설정 (null 안전 처리)
        if (study.getMembers() != null) {
            MembersDTO membersDTO = modelMapper.map(study.getMembers(), MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);
            log.info("매핑된 MembersDTO 데이터: {}", membersDTO);
        } else {
            // 스터디장 정보가 없는 경우 기본값 처리
            log.warn("스터디 {}에 스터디장 정보가 없습니다", study.getId());
            MembersDTO defaultMemberDTO = new MembersDTO();
            defaultMemberDTO.setName(study.getLeader() != null ? study.getLeader() : "알 수 없음");
            defaultMemberDTO.setEmail("");
            studyDTO.setMembersDTO(defaultMemberDTO);
        }

        // 로그인한 사용자 정보 추가
        String email = "";
        String userName = "";
        boolean isLoggedIn = false;

        if (principal != null) {
            email = principal.getName();
            userName = memberService.getUserNameByEmail(email);
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

        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("study", studyDTO);
        model.addAttribute("isStudyAuthor", isStudyAuthor);
        model.addAttribute("pendingParticipants", pendingParticipants);
        model.addAttribute("approvedParticipants", approvedParticipants);
        model.addAttribute("categories", categories);

        return "study/read";
    }

    @GetMapping("/manage")
    public String manage(Principal principal, Model model) {
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

        // 헤더용 사용자 정보
        String userName = memberService.getUserNameByEmail(email);

        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", true);
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
    public String approvalRequests(Principal principal, Model model) {
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

        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", true);
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
    public String manageStudyDetail(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirectAttributes) {
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

        // MembersDTO 수동 설정 (null 안전 처리)
        if (study.getMembers() != null) {
            MembersDTO membersDTO = modelMapper.map(study.getMembers(), MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);
            log.info("매핑된 MembersDTO 데이터: {}", membersDTO);
        } else {
            // 스터디장 정보가 없는 경우 기본값 처리
            log.warn("스터디 {}에 스터디장 정보가 없습니다", study.getId());
            MembersDTO defaultMemberDTO = new MembersDTO();
            defaultMemberDTO.setName(study.getLeader() != null ? study.getLeader() : "알 수 없음");
            defaultMemberDTO.setEmail("");
            studyDTO.setMembersDTO(defaultMemberDTO);
        }

        // 승인 대기 중인 참여자
        List<StudyParticipant> pendingParticipants = studyParticipantRepository.findByStudyIdAndStatus(
                study.getId(), StudyParticipant.ParticipantStatus.PENDING);

        // 승인된 참여자
        List<StudyParticipant> approvedParticipants = studyParticipantRepository.findByStudyIdAndStatus(
                study.getId(), StudyParticipant.ParticipantStatus.APPROVED);

        // 헤더 표시용 사용자 정보 추가
        String userName = memberService.getUserNameByEmail(email);

        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", true);
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
    public String adminRead(@PathVariable Long id, Model model, Principal principal) {
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

        // MembersDTO 수동 설정 (null 안전 처리)
        if (study.getMembers() != null) {
            MembersDTO membersDTO = modelMapper.map(study.getMembers(), MembersDTO.class);
            studyDTO.setMembersDTO(membersDTO);
            log.info("매핑된 MembersDTO 데이터: {}", membersDTO);
        } else {
            // 스터디장 정보가 없는 경우 기본값 처리
            log.warn("스터디 {}에 스터디장 정보가 없습니다", study.getId());
            MembersDTO defaultMemberDTO = new MembersDTO();
            defaultMemberDTO.setName(study.getLeader() != null ? study.getLeader() : "알 수 없음");
            defaultMemberDTO.setEmail("");
            studyDTO.setMembersDTO(defaultMemberDTO);
        }

        // 로그인한 사용자 정보 추가
        String email = principal.getName();
        String userName = memberService.getUserNameByEmail(email);

        // 승인 대기 중인 참여자 (스터디장만 볼 수 있음)
        List<StudyParticipant> pendingParticipants = studyParticipantRepository.findByStudyIdAndStatus(
                study.getId(), StudyParticipant.ParticipantStatus.PENDING);

        // 승인된 참여자
        List<StudyParticipant> approvedParticipants = studyParticipantRepository.findByStudyIdAndStatus(
                study.getId(), StudyParticipant.ParticipantStatus.APPROVED);

        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("study", studyDTO);
        model.addAttribute("isStudyAuthor", true); // 스터디장만 접근 가능하므로 true
        model.addAttribute("pendingParticipants", pendingParticipants);
        model.addAttribute("approvedParticipants", approvedParticipants);

        return "study/admin-read";
    }

    /**
     * 스터디 멤버 역할 변경 (일반 멤버 ↔ 운영진)
     * POST /study/members/{studyId}/change-role
     */
    @PostMapping("/members/{studyId}/change-role")
    public String changeMemberRole(@PathVariable Long studyId,
                                   @RequestParam Long memberId,
                                   @RequestParam String role,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {

        log.info("===========================================");
        log.info("멤버 역할 변경 요청");
        log.info("studyId={}, memberId={}, newRole={}", studyId, memberId, role);
        log.info("===========================================");

        try {
            // 로그인 체크
            if (principal == null) {
                log.error("로그인되지 않은 사용자의 역할 변경 시도");
                redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
                return "redirect:/members/login";
            }

            String email = principal.getName();
            Members currentUser = memberRepository.findByEmail(email);

            if (currentUser == null) {
                log.error("사용자를 찾을 수 없습니다: {}", email);
                redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // 스터디 조회
            Study study = studyRepository.findById(studyId).orElse(null);
            if (study == null) {
                log.error("스터디를 찾을 수 없습니다: {}", studyId);
                redirectAttributes.addFlashAttribute("errorMessage", "스터디를 찾을 수 없습니다.");
                return "redirect:/study/manage";
            }

            // 권한 체크: 스터디장인지 확인
            if (!study.getMembers().getId().equals(currentUser.getId())) {
                log.warn("스터디장이 아닌 사용자의 역할 변경 시도: userId={}, studyId={}", currentUser.getId(), studyId);
                redirectAttributes.addFlashAttribute("errorMessage", "해당 스터디의 관리 권한이 없습니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // StudyParticipant 조회
            StudyParticipant participant = studyParticipantRepository.findById(memberId).orElse(null);
            if (participant == null) {
                log.error("참여자를 찾을 수 없습니다: participantId={}", memberId);
                redirectAttributes.addFlashAttribute("errorMessage", "멤버를 찾을 수 없습니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // 참여자가 해당 스터디의 멤버인지 확인
            if (!participant.getStudy().getId().equals(studyId)) {
                log.warn("다른 스터디의 멤버 역할 변경 시도: studyId={}, participantStudyId={}",
                        studyId, participant.getStudy().getId());
                redirectAttributes.addFlashAttribute("errorMessage", "해당 스터디의 멤버가 아닙니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // 역할 변경
            com.example.studyport.constant.Role newRole =
                    com.example.studyport.constant.Role.valueOf(role.toUpperCase());

            String oldRole = participant.getRole().name();
            participant.setRole(newRole);
            studyParticipantRepository.save(participant);

            log.info("===========================================");
            log.info("멤버 역할 변경 완료");
            log.info("memberId={}, oldRole={}, newRole={}", memberId, oldRole, newRole);
            log.info("===========================================");

            String roleDescription = newRole.name().equals("STUDY_OPERATOR") ? "운영진" : "일반 멤버";
            redirectAttributes.addFlashAttribute("successMessage",
                    participant.getMember().getName() + "님을 " + roleDescription + "으로 변경했습니다.");

        } catch (IllegalArgumentException e) {
            log.error("잘못된 역할값: {}", role, e);
            redirectAttributes.addFlashAttribute("errorMessage", "잘못된 역할입니다.");
        } catch (Exception e) {
            log.error("===========================================");
            log.error("멤버 역할 변경 중 오류 발생", e);
            log.error("===========================================");
            redirectAttributes.addFlashAttribute("errorMessage", "역할 변경 중 오류가 발생했습니다. 다시 시도해주세요.");
        }

        return "redirect:/study/admin/read/" + studyId;
    }

    /**
     * 스터디 멤버 강퇴
     * POST /study/members/{studyId}/remove
     */
    @PostMapping("/members/{studyId}/remove")
    public String removeMember(@PathVariable Long studyId,
                               @RequestParam Long memberId,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {

        log.info("===========================================");
        log.info("멤버 강퇴 요청");
        log.info("studyId={}, memberId={}", studyId, memberId);
        log.info("===========================================");

        try {
            // 로그인 체크
            if (principal == null) {
                log.error("로그인되지 않은 사용자의 강퇴 시도");
                redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
                return "redirect:/members/login";
            }

            String email = principal.getName();
            Members currentUser = memberRepository.findByEmail(email);

            if (currentUser == null) {
                log.error("사용자를 찾을 수 없습니다: {}", email);
                redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // 스터디 조회
            Study study = studyRepository.findById(studyId).orElse(null);
            if (study == null) {
                log.error("스터디를 찾을 수 없습니다: {}", studyId);
                redirectAttributes.addFlashAttribute("errorMessage", "스터디를 찾을 수 없습니다.");
                return "redirect:/study/manage";
            }

            // 권한 체크: 스터디장인지 확인
            if (!study.getMembers().getId().equals(currentUser.getId())) {
                log.warn("스터디장이 아닌 사용자의 강퇴 시도: userId={}, studyId={}", currentUser.getId(), studyId);
                redirectAttributes.addFlashAttribute("errorMessage", "해당 스터디의 관리 권한이 없습니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // StudyParticipant 조회
            StudyParticipant participant = studyParticipantRepository.findById(memberId).orElse(null);
            if (participant == null) {
                log.error("참여자를 찾을 수 없습니다: participantId={}", memberId);
                redirectAttributes.addFlashAttribute("errorMessage", "멤버를 찾을 수 없습니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // 참여자가 해당 스터디의 멤버인지 확인
            if (!participant.getStudy().getId().equals(studyId)) {
                log.warn("다른 스터디의 멤버 강퇴 시도: studyId={}, participantStudyId={}",
                        studyId, participant.getStudy().getId());
                redirectAttributes.addFlashAttribute("errorMessage", "해당 스터디의 멤버가 아닙니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // 스터디장이 자신을 강퇴하는 것 방지
            if (participant.getStudy().getMembers().getId().equals(participant.getMember().getId())) {
                log.warn("스터디장이 자신을 강퇴하려고 시도: studyId={}, memberId={}", studyId, memberId);
                redirectAttributes.addFlashAttribute("errorMessage", "스터디장은 강퇴할 수 없습니다.");
                return "redirect:/study/admin/read/" + studyId;
            }

            // 멤버 강퇴 (삭제)
            String memberName = participant.getMember().getName();
            studyParticipantRepository.deleteById(memberId);

            log.info("===========================================");
            log.info("멤버 강퇴 완료");
            log.info("memberId={}, memberName={}", memberId, memberName);
            log.info("===========================================");

            redirectAttributes.addFlashAttribute("successMessage",
                    memberName + "님을 스터디에서 강퇴했습니다.");

        } catch (Exception e) {
            log.error("===========================================");
            log.error("멤버 강퇴 중 오류 발생", e);
            log.error("===========================================");
            redirectAttributes.addFlashAttribute("errorMessage", "강퇴 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
        }

        return "redirect:/study/admin/read/" + studyId;
    }
}