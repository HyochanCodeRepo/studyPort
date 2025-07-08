package com.example.studyport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/groups")
public class GroupPageController {

    @GetMapping("/create")
    public String createForm() {
        return "group/create"; // 위 HTML
    }

    @GetMapping("/my")
    public String myGroups() {
        return "group/mystudy"; // (다음 단계) 내가 만든/참여 그룹 리스트
    }
}
