package com.example.studyport.config;

import com.example.studyport.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
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
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String email = (String) oAuth2User.getAttributes().get("email");
        log.info("접속경로: {} / 이메일: {}",provider,email);

        // 회원 여부 확인
        if (memberRepository.existsByEmail(email)) {
            response.sendRedirect("/");
        } else {
            request.getSession().setAttribute("oauth2email", email);
            request.getSession().setAttribute("oauth2provider", provider);
            response.sendRedirect("/members/signup");
        }
    }
}
