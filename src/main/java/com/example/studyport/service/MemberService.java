package com.example.studyport.service;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.entity.Members;
import org.springframework.stereotype.Service;

public interface MemberService {

    public Members create(MembersDTO membersDTO);
    
    public Members authenticateUser(String email, String password);

}
