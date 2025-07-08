package com.example.studyport.config;

import jakarta.servlet.annotation.WebListener;
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
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        (AuthorizeHttpRequests) -> AuthorizeHttpRequests
                                .requestMatchers("/members/login", "/members/signup").permitAll()
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
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
