package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.entity.Category;
import com.example.studyport.service.CategoryService;
import com.example.studyport.service.CategoryServiceImpl;
import com.example.studyport.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final CategoryService categoryService;


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
    public String signupPost(MembersDTO membersDTO,  @RequestParam Long categoryId) {
        log.info(membersDTO);

        memberService.create(membersDTO);

        return  "redirect:/members/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션 무효화
        session.invalidate();
        log.info("사용자 로그아웃 완료");
        return "redirect:/";
    }
}
