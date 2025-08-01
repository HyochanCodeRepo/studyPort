package com.example.studyport.service;

import com.example.studyport.constant.Role;
import com.example.studyport.dto.MembersDTO;
import com.example.studyport.entity.Members;
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

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private ModelMapper modelMapper = new ModelMapper();
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void create(MembersDTO membersDTO) {

        Members members =
            modelMapper.map(membersDTO, Members.class);
        members.setRole(Role.USER);
        members.setPassword(passwordEncoder.encode(members.getPassword()));


        memberRepository.save(members);

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
