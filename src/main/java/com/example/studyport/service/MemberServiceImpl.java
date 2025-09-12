package com.example.studyport.service;

import com.example.studyport.constant.Role;
import com.example.studyport.dto.MembersDTO;
import com.example.studyport.entity.Category;
import com.example.studyport.entity.Members;
import com.example.studyport.repository.CategoryRepository;
import com.example.studyport.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private ModelMapper modelMapper = new ModelMapper();
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;


    @Override
    public Members create(MembersDTO membersDTO) {

        Members members = new Members();
        // 기본 필드 수동 매핑 (ModelMapper가 categories 컬렉션을 제대로 처리하지 못할 수 있음)
        members.setName(membersDTO.getName());
        members.setEmail(membersDTO.getEmail());
        members.setPhone(membersDTO.getPhone());
        members.setAddress(membersDTO.getAddress());
        members.setProvider(membersDTO.getProvider());
        members.setProviderId(membersDTO.getProviderId());
        members.setRole(Role.USER);
        members.setPassword(passwordEncoder.encode(membersDTO.getPassword()));

        // 여러 카테고리 처리
        if (membersDTO.getCategoryIds() != null && !membersDTO.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            
            for (Long categoryId : membersDTO.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));
                categories.add(category);
            }
            
            members.setCategories(categories);
        }

        return memberRepository.save(members);
    }

    @Override
    public Members authenticateUser(String email, String password) {
        Members member = memberRepository.findByEmail(email);
        
        if (member == null) {
            log.info("해당 이메일로 가입된 사용자가 없습니다: " + email);
            return null;
        }
        
        if (passwordEncoder.matches(password, member.getPassword())) {
            log.info("로그인 성공: " + member.getEmail());
            return member;
        } else {
            log.info("비밀번호가 일치하지 않습니다: " + email);
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{

        Members members = memberRepository.findByEmail(email);

        if (members == null){
            log.info("현재 입력하신 email로는 가입된 정보가 없습니다.");
            throw new UsernameNotFoundException(email);
        }
        log.info("로그인 시도하는 사람 : " + members.getEmail());

        String role = "";

        if (members.getRole() == Role.ADMIN) {
            log.info("관리자 로그인 시도중");
            role = members.getRole().name();
        } else {
            log.info("일반 유저 로그인 시도중");
            role = members.getRole().name();
        }
        UserDetails user =
                User.builder().username(
                                members.getEmail())
                        .password(members.getPassword())
                        .roles(role)
                        .build();

        return user;

    }
    
    @Override
    public Members findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    @Override
    public Members updateMember(Members member) {
        log.info("회원 정보 업데이트: {}", member.getEmail());
        return memberRepository.save(member);
    }
    
    @Override
    public boolean validatePassword(String email, String password) {
        Members member = memberRepository.findByEmail(email);
        
        if (member == null) {
            log.info("해당 이메일로 가입된 사용자가 없습니다: " + email);
            return false;
        }
        
        boolean isValid = passwordEncoder.matches(password, member.getPassword());
        log.info("비밀번호 검증 결과 for {}: {}", email, isValid);
        return isValid;
    }
    
    @Override
    public boolean changePassword(Members member, String newPassword) {
        try {
            // 새 비밀번호 암호화
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            member.setPassword(encodedNewPassword);
            
            // 데이터베이스에 저장
            memberRepository.save(member);
            
            log.info("비밀번호 변경 성공: {}", member.getEmail());
            return true;
        } catch (Exception e) {
            log.error("비밀번호 변경 실패: {}", member.getEmail(), e);
            return false;
        }
    }
    
    @Override
    public String getUserNameByEmail(String email) {
        Members member = memberRepository.findByEmail(email);
        if (member != null && member.getName() != null) {
            return member.getName();
        }
        return email; // 이름이 없으면 이메일을 반환
    }
}
