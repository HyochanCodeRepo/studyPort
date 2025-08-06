package com.example.studyport.config;

import com.example.studyport.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        // 회원 여부 확인
//        if (memberRepository.existsByEmail(email)) {
        if (memberRepository.findByEmail(email)!=null) {
            response.sendRedirect("/");
        } else { //todo 일단 세션에 저장해두고 회원가입 페이지에서 가져오기 ("/member/signup")
            request.getSession().setAttribute("oauth2email", email);
            request.getSession().setAttribute("oauth2provider", provider);
            response.sendRedirect("/members/signup");
        }
    }
}
