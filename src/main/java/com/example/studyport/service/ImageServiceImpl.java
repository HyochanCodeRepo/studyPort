package com.example.studyport.service;

import com.example.studyport.dto.ImageDTO;
import com.example.studyport.entity.Image;
import com.example.studyport.entity.Study;
import com.example.studyport.repository.ImageRepository;
import com.example.studyport.repository.StudyRepository;
import lombok.extern.log4j.Log4j2;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 이미지 서비스 구현
 * shop 프로젝트의 ImgService 방식을 동일하게 적용
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final StudyRepository studyRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    /**
     * 이미지 등록/업데이트
     * 
     * @param multipartFile 업로드 파일
     * @param study_id 스터디 ID
     * @param repImgYn 대표 이미지 여부 ("Y" 또는 null)
     * @return 저장된 이미지 정보 DTO
     * @throws IOException 파일 저장 실패 시
     */
    @Override
    public ImageDTO register(MultipartFile multipartFile, Long study_id, String repImgYn) throws IOException {
        log.info("=== ImageService.register() 시작 ===");
        log.info("스터디 ID: {}", study_id);
        log.info("파일명: {}", multipartFile.getOriginalFilename());
        log.info("대표이미지 여부: {}", repImgYn);

        try {
            // 1단계: 파일을 물리적으로 저장하고 ImageDTO 생성
            ImageDTO imageDTO = fileService.register(multipartFile);
            log.info("파일 저장 완료 - 저장된 이미지명: {}", imageDTO.getImgName());

            // 2단계: ImageDTO를 Image 엔티티로 변환
            Image image = modelMapper.map(imageDTO, Image.class);

            // 3단계: 스터디 조회 (참조 대상 찾기)
            Study study = studyRepository.findById(study_id)
                    .orElseThrow(() -> new EntityNotFoundException("스터디를 찾을 수 없습니다. ID: " + study_id));
            log.info("스터디 조회 완료: {}", study.getName());

            // 4단계: 기존 대표이미지 확인
            Image existingImg = null;
            if (repImgYn != null) {
                existingImg = imageRepository.findByStudy_IdAndRepImgYn(study.getId(), repImgYn);
                log.info("기존 대표이미지 조회: {}", existingImg != null ? "존재" : "없음");
            }

            // 5단계: 대표이미지가 있으면 업데이트, 없으면 새로 저장
            if (existingImg != null) {
                // 기존 이미지 파일 물리적 삭제
                String path = existingImg.getImgUrl() + existingImg.getImgName();
                fileService.del(path);
                log.info("기존 이미지 파일 삭제: {}", path);

                // 기존 이미지 엔티티 업데이트
                existingImg.setImgUrl(imageDTO.getImgUrl());
                existingImg.setImgName(imageDTO.getImgName());
                existingImg.setOriImgName(imageDTO.getOriImgName());

                imageRepository.save(existingImg);
                imageDTO = modelMapper.map(existingImg, ImageDTO.class);
                log.info("기존 이미지 업데이트 완료");

            } else {
                // 새로운 이미지 저장
                image.setRepImgYn(repImgYn);
                image.setStudy(study);

                image = imageRepository.save(image);
                imageDTO = modelMapper.map(image, ImageDTO.class);
                log.info("새 이미지 저장 완료 - 이미지 ID: {}", image.getId());
            }

            log.info("=== ImageService.register() 완료 ===");
            return imageDTO;

        } catch (IOException e) {
            log.error("파일 저장 중 IO 오류 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("이미지 등록 중 오류 발생", e);
            throw new RuntimeException("이미지 등록에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
