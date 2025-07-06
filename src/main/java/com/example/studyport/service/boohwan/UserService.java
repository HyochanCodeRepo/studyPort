package com.example.studyport.service.boohwan;

import com.example.studyport.dto.UserDTO;
import com.example.studyport.entity.User;
import com.example.studyport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    // 회원가입
    public void registerUser(UserDTO userDTO) {
        if (userRepository.findByUserEmail(userDTO.getUserEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 비밀번호는 평문 그대로 저장
        // 생년월일 변환 및 매핑
        User user = modelMapper.map(userDTO, User.class);
        user.setUserBirth(LocalDate.parse(userDTO.getUserBirth())); // DTO는 String, Entity는 LocalDate

        userRepository.save(user);
    }

    // 로그인
    public boolean login(String email, String password) {
        return userRepository.findByUserEmail(email)
                .map(user -> user.getUserPassword().equals(password)) // 평문 비교
                .orElse(false);
    }
}
