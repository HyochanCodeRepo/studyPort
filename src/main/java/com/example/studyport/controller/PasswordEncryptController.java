package com.example.studyport.controller;

import com.example.studyport.entity.Members;
import com.example.studyport.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Log4j2
public class PasswordEncryptController {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /**
     * 평문 비밀번호를 BCrypt로 암호화
     * 사용법: GET /api/password/encrypt?password=1234
     */
    @GetMapping("/encrypt")
    public Map<String, String> encryptPassword(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        Map<String, String> result = new HashMap<>();
        result.put("plainPassword", password);
        result.put("encodedPassword", encoded);
        log.info("비밀번호 암호화: {} -> {}", password, encoded);
        return result;
    }

    /**
     * 모든 회원의 비밀번호를 암호화 (평문인 경우만)
     * 사용법: POST /api/password/encrypt-all
     *
     * IMPORTANT: 이 엔드포인트는 매우 위험합니다!
     * 기존 평문 비밀번호를 알고 있어야만 사용 가능합니다.
     * 실제로는 사용자가 직접 비밀번호를 재설정해야 합니다.
     */
    @PostMapping("/encrypt-all")
    public Map<String, Object> encryptAllPasswords(@RequestParam String defaultPassword) {
        Map<String, Object> result = new HashMap<>();

        List<Members> allMembers = memberRepository.findAll();
        int updatedCount = 0;

        for (Members member : allMembers) {
            // BCrypt로 암호화된 비밀번호는 "$2a$" 또는 "$2b$"로 시작
            if (member.getPassword() != null && !member.getPassword().startsWith("$2")) {
                log.info("평문 비밀번호 발견: {} (이메일: {})", member.getPassword(), member.getEmail());

                // 평문 비밀번호를 암호화
                String encoded = passwordEncoder.encode(member.getPassword());
                member.setPassword(encoded);
                memberRepository.save(member);

                updatedCount++;
                log.info("비밀번호 암호화 완료: {}", member.getEmail());
            }
        }

        result.put("totalMembers", allMembers.size());
        result.put("updatedMembers", updatedCount);
        result.put("message", updatedCount + "명의 회원 비밀번호를 암호화했습니다.");

        return result;
    }

    /**
     * 특정 회원의 비밀번호를 새로운 값으로 재설정
     * 사용법: POST /api/password/reset?email=test@test.com&newPassword=1234
     */
    @PostMapping("/reset")
    public Map<String, String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        Map<String, String> result = new HashMap<>();

        Members member = memberRepository.findByEmail(email);

        if (member == null) {
            result.put("status", "error");
            result.put("message", "해당 이메일의 회원을 찾을 수 없습니다.");
            return result;
        }

        // 새 비밀번호를 암호화해서 저장
        String encoded = passwordEncoder.encode(newPassword);
        member.setPassword(encoded);
        memberRepository.save(member);

        result.put("status", "success");
        result.put("message", email + "의 비밀번호가 재설정되었습니다.");
        result.put("encodedPassword", encoded);

        log.info("비밀번호 재설정 완료: {}", email);

        return result;
    }
}
