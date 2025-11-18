package com.example.studyport.service;

import com.example.studyport.dto.StudyDTO;
import com.example.studyport.entity.Study;
import com.example.studyport.repository.MemberRepository;
import com.example.studyport.repository.StudyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {
    
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Override
    public void createStudy(StudyDTO studyDTO) {
        log.info("스터디 크리에이트 진입");

        Study study = modelMapper.map(studyDTO, Study.class);
        studyRepository.save(study);
    }

    @Override
    public void create(StudyDTO studyDTO, MultipartFile mainimg) throws IOException {
        log.info("스터디 생성 서비스 진입");
        log.info("StudyDTO: {}", studyDTO);
        log.info("이미지 파일: {}", mainimg != null ? mainimg.getOriginalFilename() : "없음");

        // StudyDTO를 Study 엔티티로 변환
        Study study = modelMapper.map(studyDTO, Study.class);

        // 비공개 스터디 설정
        if (studyDTO.getPassword() != null && !studyDTO.getPassword().trim().isEmpty()) {
            study.setIsPrivate(true);
        } else {
            study.setIsPrivate(false);
        }

        // null 방지를 위한 추가 체크
        if (study.getIsPrivate() == null) {
            study.setIsPrivate(false);
        }

        // 스터디장 이름 설정 (성능 향상을 위해 직접 저장)
        if (study.getMembers() != null && study.getMembers().getName() != null) {
            study.setLeader(study.getMembers().getName());
            log.info("스터디장 설정: {}", study.getLeader());
        } else {
            study.setLeader("알 수 없음");
            log.warn("스터디장 정보가 없어 기본값으로 설정");
        }

        // 스터디 저장
        Study savedStudy = studyRepository.save(study);
        log.info("스터디 저장 완료: {}", savedStudy.getId());

        // 이미지가 있을 경우에만 이미지 저장
        if (mainimg != null && !mainimg.isEmpty() && !mainimg.getOriginalFilename().isEmpty()) {
            log.info("이미지 저장 진입: {}", mainimg.getOriginalFilename());
            try {
                imageService.register(mainimg, savedStudy.getId(), "Y");
                log.info("이미지 저장 완료");
            } catch (Exception e) {
                log.error("이미지 저장 실패, 하지만 스터디는 생성됨", e);
                // 이미지 저장 실패해도 스터디는 생성되도록 함
            }
        } else {
            log.info("이미지 없이 스터디만 생성");
        }
    }

    @Override
    public List<StudyDTO> searchByKeyword(String keyword) {
        log.info("검색 키워드: {}", keyword);

        List<Study> studies = studyRepository.searchByKeyword(keyword);

        // Entity를 DTO로 변환
        return studies.stream()
                .map(study -> modelMapper.map(study, StudyDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyDTO> getAllStudies() {
        log.info("전체 스터디 조회");

        List<Study> studies = studyRepository.findAll();

        // Entity를 DTO로 변환
        return studies.stream()
                .map(study -> modelMapper.map(study, StudyDTO.class))
                .collect(Collectors.toList());
    }
}
