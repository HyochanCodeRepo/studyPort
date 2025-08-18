package com.example.studyport.controller;

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
        if(principal!=null){
            String email = principal.getName();
            log.info("email: " + email);
            log.info("email: " + email);
            log.info("principal: " + principal);
            model.addAttribute("email", email);
        }
        // todo
        //  OAuthAttribute.java에 있는 ofNaver(), ofGoogle() 리턴 부분에서
        //  .nameAttributeKey 값만 바꿔주면 principal.getName()이 어떤 데이터를 가져올지 정할 수 있어요
        //  지금은 이메일을 가져오게 해놨는데 로그인 환영 인사에서만 쓸 거면 name으로 바꿔도 좋겠어요


        return "main";
    }

    @GetMapping("/main2")
    public String mainTest(){
        return "main2";
    }
}
