package com.example.studyport.controller;

import com.example.studyport.dto.StudyDTO;
import com.example.studyport.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
@Log4j2
public class StudyApiController {

    private final StudyService studyService;

    /**
     * 스터디 검색 API
     * @param keyword 검색어 (스터디명 또는 지역)
     * @return 검색 결과 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<StudyDTO>> searchStudies(
            @RequestParam(required = false, defaultValue = "") String keyword) {

        log.info("검색 API 호출 - 키워드: {}", keyword);

        List<StudyDTO> results;

        // 빈 검색어면 전체 목록 반환
        if (keyword.trim().isEmpty()) {
            results = studyService.getAllStudies();
            log.info("전체 스터디 조회 - 결과 수: {}", results.size());
        } else {
            results = studyService.searchByKeyword(keyword);
            log.info("검색 완료 - 결과 수: {}", results.size());
        }

        return ResponseEntity.ok(results);
    }
}
