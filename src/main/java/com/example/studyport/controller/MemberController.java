package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/login")
    public String login() {
        return "members/login";
    }

    @PostMapping("/login")
    public String login(MembersDTO membersDTO) {

        log.info(membersDTO.toString());
        log.info(membersDTO.toString());
        log.info(membersDTO.toString());
        return "/";
    }


    //fixme 세션에서 끌고와서 있으면 모델로 넘겨줍니다...
    @GetMapping("/signup")
    public String signupGet(MembersDTO membersDTO, HttpSession session, Model model) {
        String email = String.valueOf(session.getAttribute("oauth2email")); //"null"
        String provider = String.valueOf(session.getAttribute("oauth2provider"));
        if (!"null".equals(email) && !"null".equals(provider)) {
            model.addAttribute("email",email);
            model.addAttribute("prov",provider);
        }

        return "members/signup";
    }

    @PostMapping("/signup")
    public String signupPost(MembersDTO membersDTO) {
        log.info(membersDTO);

        memberService.create(membersDTO);

        return "members/signup";
    }
}
