package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping("/signup")
    public String signupGet(MembersDTO membersDTO) {

        return "members/signup";
    }

    @PostMapping("/signup")
    public String signupPost(MembersDTO membersDTO) {
        log.info(membersDTO);

        memberService.create(membersDTO);

        return "members/signup";
    }
}
