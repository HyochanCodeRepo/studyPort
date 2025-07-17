//package com.example.studyport.controller.ApiController;
//
//import com.example.studyport.dto.UserDTO;
//import com.example.studyport.service.boohwan.UserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@Log4j2
//@RestController
//@RequestMapping("/api/users")
//@RequiredArgsConstructor
//public class UserController {
//
//    private final UserService userService;
//
//    @PostMapping("/register")
//    public String register( UserDTO userDTO) {
//       log.info(userDTO);
//       log.info(userDTO);
//       log.info(userDTO);
//
//
//       return null;
//
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
//        boolean result = userService.login(userDTO.getUserEmail(), userDTO.getUserPassword());
//        if (result) return ResponseEntity.ok("로그인 성공!");
//        else return ResponseEntity.status(401).body("로그인 실패");
//    }
//}
