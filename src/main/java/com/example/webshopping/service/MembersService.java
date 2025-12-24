package com.example.webshopping.service;

import com.example.studyport.dto.MembersDTO;

public interface MembersService {
    
    void create(MembersDTO membersDTO);
    
    boolean existsByEmail(String email);
}
