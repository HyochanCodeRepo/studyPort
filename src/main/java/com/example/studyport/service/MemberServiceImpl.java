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

import java.awt.event.FocusAdapter;

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
    public void create(MembersDTO membersDTO) {

        Members members =
            modelMapper.map(membersDTO, Members.class);
        members.setRole(Role.USER);
        members.setPassword(passwordEncoder.encode(members.getPassword()));

        // ⭐️ categoryId가 DTO에 들어있을 때!
        Long categoryId = membersDTO.getCategoryId(); // getter 필요
        // category FK 처리 (null 체크 포함)
        if(categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));
            members.setCategory(category);
        }

        memberRepository.save(members);

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
}
