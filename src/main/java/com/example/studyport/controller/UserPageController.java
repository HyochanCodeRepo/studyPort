package com.example.studyport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserPageController {

    // 회원가입 폼 페이지 (GET)
    @GetMapping("/register")
    public String registerForm() {
        return "user/register"; // src/main/resources/templates/user/register.html
    }

    // 로그인 폼 페이지 (GET)
    @GetMapping("/login")
    public String loginForm() {
        return "user/login"; // src/main/resources/templates/user/login.html
    }
}

