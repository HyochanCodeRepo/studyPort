package com.example.studyport.config;

import com.example.studyport.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    public CustomOAuth2SuccessHandler(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        // 회원 여부 확인
//        if (memberRepository.existsByEmail(email)) {
        if (memberRepository.findByEmail(email)!=null) {
            response.sendRedirect("/");
        } else {
            request.getSession().setAttribute("oauthemail", email); //todo 이메일 세션에 저장 (회원가입에서 가쟈오기)
            response.sendRedirect("/members/signup"); //todo 회원가입 지금은 "/members/signup"
        }
    }
}
