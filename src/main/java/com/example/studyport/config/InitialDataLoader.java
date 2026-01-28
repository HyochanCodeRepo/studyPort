package com.example.studyport.config;

import com.example.studyport.constant.Role;
import com.example.studyport.entity.Category;
import com.example.studyport.entity.Members;
import com.example.studyport.repository.CategoryRepository;
import com.example.studyport.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * 초기 데이터 자동 생성
 * - 기본 카테고리 10개 자동 생성 (DB에 없을 경우)
 * - 시연용 관리자 계정 자동 생성 (admin@test / 1234)
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class InitialDataLoader {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("===========================================");
        log.info("초기 데이터 로더 시작");
        log.info("===========================================");

        // 1. 기본 카테고리 체크 및 생성
        initializeCategories();

        // 2. 시연 계정 체크 및 생성
        initializeDemoAccount();

        log.info("===========================================");
        log.info("초기 데이터 로더 완료");
        log.info("===========================================");
    }

    /**
     * 기본 카테고리 초기화
     */
    private void initializeCategories() {
        long categoryCount = categoryRepository.count();
        log.info("현재 카테고리 개수: {}", categoryCount);

        if (categoryCount == 0) {
            log.info("기본 카테고리가 없습니다. 생성을 시작합니다...");

            List<String> defaultCategories = Arrays.asList(
                    "프로그래밍/개발",
                    "디자인",
                    "언어 학습",
                    "취업/면접",
                    "자격증",
                    "운동/건강",
                    "독서/토론",
                    "금융/투자",
                    "창업/비즈니스",
                    "취미/여가"
            );

            for (String categoryName : defaultCategories) {
                Category category = new Category();
                category.setName(categoryName);
                categoryRepository.save(category);
                log.info("카테고리 생성: {}", categoryName);
            }

            log.info("기본 카테고리 {} 개 생성 완료", defaultCategories.size());
        } else {
            log.info("기본 카테고리가 이미 존재합니다. 생성을 건너뜁니다.");
        }
    }

    /**
     * 시연용 관리자 계정 초기화
     */
    private void initializeDemoAccount() {
        String demoEmail = "test@test";
        Members existingMember = memberRepository.findByEmail(demoEmail);

        if (existingMember == null) {
            log.info("시연 계정({})이 없습니다. 생성을 시작합니다...", demoEmail);

            Members demoAdmin = new Members();
            demoAdmin.setEmail(demoEmail);
            demoAdmin.setPassword(passwordEncoder.encode("Hyochan!23"));
            demoAdmin.setName("테스트관리자");
            demoAdmin.setRole(Role.ADMIN);
            memberRepository.save(demoAdmin);
        } else {
            log.info("시연 계정({})이 이미 존재합니다. 생성을 건너뜁니다.", demoEmail);
        }
    }
}
