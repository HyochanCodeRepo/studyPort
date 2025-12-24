package com.example.webshopping.service;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.entity.Members;
import com.example.studyport.repository.MemberRepository;
import com.example.studyport.constant.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class MembersServiceImpl implements MembersService {
    
    private final MemberRepository membersRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void create(MembersDTO membersDTO) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(membersDTO.getPassword());
        membersDTO.setPassword(encodedPassword);
        
        Members member = modelMapper.map(membersDTO, Members.class);
        member.setRole(Role.USER);  // 기본 역할 설정
        
        membersRepository.save(member);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return membersRepository.existsByEmail(email);
    }
}
