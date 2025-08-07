package com.example.studyport.service;

import com.example.studyport.constant.Role;
import com.example.studyport.dto.CategoryDTO;
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

import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private ModelMapper modelMapper = new ModelMapper();
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;



    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

}





