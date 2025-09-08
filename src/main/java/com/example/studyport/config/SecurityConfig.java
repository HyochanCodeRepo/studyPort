package com.example.studyport.config;

import com.example.studyport.service.CustomOAuth2UserService;
import jakarta.servlet.annotation.WebListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@WebListener
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2RequestResolver customOAuth2RequestResolver;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        (AuthorizeHttpRequests) -> AuthorizeHttpRequests
                                .requestMatchers("/members/login", "/members/signup").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().permitAll()

                )
                .csrf(csrf -> csrf.disable())
                .formLogin(
                        formLogin -> formLogin.loginPage("/members/login")
                                .defaultSuccessUrl("/")
                                .usernameParameter("email")
                )
                .logout(logout -> logout.logoutUrl("/members/logout")
                        .logoutSuccessUrl("/members/login").invalidateHttpSession(true)
                )
                .exceptionHandling(
                        a -> a.accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                                .authorizationRequestResolver(customOAuth2RequestResolver))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("automatic-login-security-key")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60*60*24*3)); //3일 동안 저장
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
