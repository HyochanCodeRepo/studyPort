package com.example.studyport.service;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.entity.Members;
import org.springframework.stereotype.Service;

public interface MemberService {

    public Members create(MembersDTO membersDTO);
    
    public Members authenticateUser(String email, String password);
    
    public Members findByEmail(String email);
    
    public Members updateMember(Members member);
    
    public boolean validatePassword(String email, String password);
    
    public boolean changePassword(Members member, String newPassword);
    
    public String getUserNameByEmail(String email);

}
