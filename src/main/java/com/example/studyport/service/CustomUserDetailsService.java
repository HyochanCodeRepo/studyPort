package com.example.studyport.service;

import com.example.studyport.entity.Members;
import com.example.studyport.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Spring Security 로그인 처리: " + email);

        // 이메일로 회원 조회
        Members member = memberRepository.findByEmail(email);

        if (member == null) {
            log.error("사용자를 찾을 수 없습니다: " + email);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        }

        log.info("회원 조회 성공: " + member.getEmail());
        log.info("회원 역할: " + member.getRole());
        log.info("회원 비밀번호 (암호화됨): " + (member.getPassword() != null ? member.getPassword().substring(0, Math.min(20, member.getPassword().length())) + "..." : "null"));
        log.info("비밀번호가 BCrypt로 암호화되어 있는가? " + (member.getPassword() != null && member.getPassword().startsWith("$2")));

        // 권한 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
            log.info("부여된 권한: ROLE_" + member.getRole().name());
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            log.info("역할이 null이므로 기본 권한 부여: ROLE_USER");
        }

        // Spring Security의 User 객체 반환
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword()) // 이미 암호화된 비밀번호
                .authorities(authorities)
                .build();
    }
}
