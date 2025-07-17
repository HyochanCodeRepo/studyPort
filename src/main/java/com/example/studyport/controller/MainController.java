package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String main(Principal principal, Model model, HttpSession session) {
        String  email = "";
        MembersDTO user = (MembersDTO) session.getAttribute("user");
        if (user != null) {
            email = user.getEmail();
        } else {
            email = principal.getName();
//            session.setAttribute("user", principal);
        }
        log.info("email: " + email);
        log.info("email: " + email);
        log.info("principal: " + principal);
        model.addAttribute("email", email);
        return "main";
    }

    @GetMapping("/main")
    public String mainTest(){
        return "main2";
    }
}
