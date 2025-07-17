package com.example.studyport.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String main(Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User, Model model, HttpSession session) {
        String  email = "";
        if (oAuth2User != null) {
            email = oAuth2User.getAttribute("email").toString();
        } else {
            email = principal.getName();
        }

        session.setAttribute("user", principal);

        log.info("email: " + email);
        log.info("email: " + email);
        log.info("principal: " + principal);
        model.addAttribute("email", email);
        return "main";
    }

    @GetMapping("/main2")
    public String mainTest(){
        return "main2";
    }
}
