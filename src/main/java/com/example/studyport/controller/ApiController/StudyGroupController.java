package com.example.studyport.controller.ApiController;

import com.example.studyport.dto.StudyGroupDTO;
import com.example.studyport.entity.User;
import com.example.studyport.service.boohwan.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    /*-------------------------------------------------
     * 1. 그룹 생성 (파일 O / X 모두 수용)
     *   - 현재 로그인 세션이 아직 연결되지 않았으므로
     *     ownerId 를 hidden 필드로 받아 임시 사용
     *------------------------------------------------*/
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createGroup(
            @RequestParam(required = false) Long ownerId,                      // ✔ hidden 으로 전송
            @RequestParam String topic,
            @RequestParam String title,
            @RequestParam Integer capacity,
            @RequestParam(required = false) String levelTag,
            @RequestParam(required = false, defaultValue = "false")
            Boolean isPrivate,                               // 디폴트 false
            @RequestParam(required = false) String description,
            @RequestParam String iconName,
            @RequestParam(required = false) MultipartFile researchFile
    ) {

        /*view 에서 임시로 만든 hidden 파라미터가 들어 왔는지 확인 하는 메서드*/
        if (ownerId == null) {
            System.out.println("⚠ ownerId is NULL!");
            return ResponseEntity.badRequest().body("ownerId 누락");
        }


        try {
            /* 1) 파일 저장 (선택) */
            String filePath = null;
            if (researchFile != null && !researchFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + researchFile.getOriginalFilename();
                Path dir  = Paths.get("uploads");
                if (Files.notExists(dir)) Files.createDirectories(dir);
                Path dest = dir.resolve(fileName);
                Files.copy(researchFile.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
                filePath = dest.toString();
            }

            /* 2) DTO 빌드 */
            StudyGroupDTO dto = StudyGroupDTO.builder()
                    .title(title)
                    .topic(topic)
                    .capacity(capacity)
                    .levelTag(levelTag)
                    .isPrivate(isPrivate)
                    .description(description)
                    .iconName(iconName)
                    .researchFilePath(filePath)
                    .build();

            /* 3) 저장 */
            studyGroupService.createGroup(ownerId, dto);
            return ResponseEntity.ok("그룹 생성 완료!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("에러: " + e.getMessage());
        }
    }

    /*-------------------------------------------------
     * 2. 내가 만든 그룹 목록
     *    ― 로그인 세션 완성 전까지는 ownerId 파라미터 사용
     *------------------------------------------------*/
    @GetMapping("/my")
    public List<StudyGroupDTO> myGroups(@RequestParam Long ownerId) {
        return studyGroupService.findMyGroups(ownerId);
    }

    /*  ✅  로그인 세션을 붙인 뒤에는
            위 파라미터를 아래처럼 교체하면 됩니다.

        public List<StudyGroupDTO> myGroups(@AuthenticationPrincipal User loginUser) {
            return studyGroupService.findMyGroups(loginUser.getUno());
        }
    */
}

