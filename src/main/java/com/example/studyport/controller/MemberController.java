package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.entity.Category;
import com.example.studyport.entity.Members;
import com.example.studyport.repository.MemberRepository;
import com.example.studyport.service.CategoryService;
import com.example.studyport.service.CategoryServiceImpl;
import com.example.studyport.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final CategoryService categoryService;
    private final MemberRepository memberRepository;


    @GetMapping("/login")
    public String login() {

        return "members/login";
    }

    @PostMapping("/login")
    public String login(MembersDTO membersDTO, HttpSession session, Model model) {
        
        log.info("로그인 시도: " + membersDTO.getEmail());
        
        // 사용자 인증
        var authenticatedMember = memberService.authenticateUser(
            membersDTO.getEmail(), 
            membersDTO.getPassword()
        );
        
        if (authenticatedMember != null) {
            // 인증 성공 - 세션에 사용자 정보 저장
            session.setAttribute("loggedInUser", authenticatedMember);
            session.setAttribute("userEmail", authenticatedMember.getEmail());
            session.setAttribute("userName", authenticatedMember.getName());
            log.info("로그인 성공: " + authenticatedMember.getEmail());
            return "redirect:/";
        } else {
            // 인증 실패
            log.info("로그인 실패: " + membersDTO.getEmail());
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "members/login";
        }
    }


    //fixme 세션에서 끌고와서 있으면 모델로 넘겨줍니다...
    @GetMapping("/signup")
    public String signupGet(MembersDTO membersDTO, HttpSession session, Model model) {

        //기존 소셜(세션)처리
        String email = String.valueOf(session.getAttribute("oauth2email")); //"null"
        String provider = String.valueOf(session.getAttribute("oauth2provider"));
        if (!"null".equals(email) && !"null".equals(provider)) {
            model.addAttribute("email",email);
            model.addAttribute("prov",provider);
        }
        // ⭐️ 카테고리 목록을 모델에 추가
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        return "members/signup";
    }

    @PostMapping("/signup")
    public String signupPost(MembersDTO membersDTO, @RequestParam String categoryIds, HttpSession session) {
        log.info(membersDTO);
        log.info("선택된 카테고리 IDs: " + categoryIds);
        
        // 쉼표로 구분된 카테고리 ID 문자열을 List<Long>으로 변환
        if (categoryIds != null && !categoryIds.trim().isEmpty()) {
            try {
                List<Long> categoryIdList = Arrays.stream(categoryIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
                
                // 최대 3개 제한 확인
                if (categoryIdList.size() > 3) {
                    categoryIdList = categoryIdList.subList(0, 3);
                }
                
                membersDTO.setCategoryIds(categoryIdList);
            } catch (NumberFormatException e) {
                log.error("카테고리 ID 파싱 오류: " + categoryIds, e);
            }
        }

        // 회원가입 처리
        Members createdMember = memberService.create(membersDTO);
        
        // 회원가입 성공 시 세션에 사용자 정보 저장 (자동 로그인 효과)
        session.setAttribute("loggedInUser", createdMember);
        session.setAttribute("userEmail", createdMember.getEmail());
        session.setAttribute("userName", createdMember.getName());
        log.info("회원가입 성공 및 자동 로그인: " + createdMember.getEmail());

        return  "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션 무효화
        session.invalidate();
        log.info("사용자 로그아웃 완료");
        return "redirect:/";
    }
    
    @GetMapping("/profile")
    public String profile(Principal principal, Model model, HttpSession session) {
        log.info("내정보 변경 페이지 진입");
        
        // 로그인 체크
        if (principal == null) {
            log.info("로그인되지 않은 사용자의 프로필 페이지 접근 시도");
            return "redirect:/members/login";
        }
        
        // 사용자 정보 조회
        Members member = memberService.findByEmail(principal.getName());
        if (member == null) {
            log.error("사용자를 찾을 수 없습니다: {}", principal.getName());
            return "redirect:/members/login";
        }
        
        // 헤더 표시용 사용자 정보 추가 (메인 페이지와 동일한 로직)
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
        
        model.addAttribute("email", email);
        model.addAttribute("userName", userName);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("member", member);
        
        return "members/profile";
    }
    
    /**
     * 프로필 업데이트 API
     */
    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, String> requestData, 
                                                              Principal principal, 
                                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("프로필 업데이트 요청: {}", requestData);
            
            // 로그인 체크
            if (principal == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(401).body(response);
            }
            
            // 현재 사용자 조회
            Members currentMember = memberService.findByEmail(principal.getName());
            if (currentMember == null) {
                response.put("success", false);
                response.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response);
            }
            
            // 업데이트할 데이터 가져오기
            String newName = requestData.get("name");
            String newAddress = requestData.get("address");
            
            // 필수 필드 검증
            if (newName == null || newName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "이름은 필수 입력 항목입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 데이터 업데이트
            boolean updated = false;
            
            // 이름 업데이트
            if (!newName.equals(currentMember.getName())) {
                currentMember.setName(newName.trim());
                updated = true;
                log.info("이름 변경: {} -> {}", currentMember.getName(), newName.trim());
            }
            
            // 주소 업데이트 (null이나 빈 문자열도 허용)
            String trimmedAddress = newAddress != null ? newAddress.trim() : "";
            if (!trimmedAddress.equals(currentMember.getAddress() != null ? currentMember.getAddress() : "")) {
                currentMember.setAddress(trimmedAddress.isEmpty() ? null : trimmedAddress);
                updated = true;
                log.info("주소 변경: {} -> {}", currentMember.getAddress(), trimmedAddress);
            }
            
            // 변경사항이 있을 경우만 저장
            if (updated) {
                Members updatedMember = memberService.updateMember(currentMember);
                
                // 세션 정보도 업데이트
                if (session.getAttribute("userName") != null) {
                    session.setAttribute("userName", updatedMember.getName());
                    session.setAttribute("loggedInUser", updatedMember);
                }
                
                log.info("프로필 업데이트 완료: {}", updatedMember.getEmail());
                
                response.put("success", true);
                response.put("message", "정보가 성공적으로 수정되었습니다.");
            } else {
                response.put("success", true);
                response.put("message", "변경된 정보가 없습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("프로필 업데이트 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 비밀번호 변경 API
     */
    @PostMapping("/profile/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> requestData,
                                                              Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("비밀번호 변경 요청");
            
            // 로그인 체크
            if (principal == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(401).body(response);
            }
            
            // 현재 사용자 조회
            Members currentMember = memberService.findByEmail(principal.getName());
            if (currentMember == null) {
                response.put("success", false);
                response.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response);
            }
            
            // 비밀번호 데이터 가져오기
            String currentPassword = requestData.get("currentPassword");
            String newPassword = requestData.get("newPassword");
            
            // 필수 필드 검증
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "현재 비밀번호를 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "새 비밀번호를 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (newPassword.length() < 8) {
                response.put("success", false);
                response.put("message", "새 비밀번호는 8자리 이상이어야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 현재 비밀번호 확인
            boolean isPasswordValid = memberService.validatePassword(currentMember.getEmail(), currentPassword);
            if (!isPasswordValid) {
                response.put("success", false);
                response.put("message", "현재 비밀번호가 올바르지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 새 비밀번호가 현재 비밀번호와 같은지 확인
            if (currentPassword.equals(newPassword)) {
                response.put("success", false);
                response.put("message", "새 비밀번호는 현재 비밀번호와 달라야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 비밀번호 변경
            boolean passwordChanged = memberService.changePassword(currentMember, newPassword);
            if (passwordChanged) {
                log.info("비밀번호 변경 완료: {}", currentMember.getEmail());
                response.put("success", true);
                response.put("message", "비밀번호가 성공적으로 변경되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "비밀번호 변경에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
