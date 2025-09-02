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

@Service
@RequiredArgsConstructor
@Log4j2
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final StudyRepository studyRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    @Override
    public ImageDTO register(MultipartFile multipartFile, Long study_id, String repImgYn) throws IOException {
        log.info("이미지 등록 시작 - 스터디 ID: {}, 대표이미지: {}", study_id, repImgYn);
        
        // 사진을 물리적으로 저장 후 저장한 내용을 바탕으로 dto 만들어서 반환
        ImageDTO imageDTO = fileService.register(multipartFile);
        
        // 반환값을 가지고 (imgDTO) db에 저장
        Image image = modelMapper.map(imageDTO, Image.class);

        // 참조대상 찾기
        Study study = studyRepository.findById(study_id).orElseThrow(EntityNotFoundException::new);

        // 대표이미지 처리
        Image existingImg = null;
        if (repImgYn != null) {
            existingImg = imageRepository.findByStudy_IdAndRepImgYn(study.getId(), repImgYn);
        }
        
        if (existingImg != null) {
            // 기존 대표이미지가 있으면 파일 삭제 후 업데이트
            String path = existingImg.getImgUrl() + existingImg.getImgName();
            fileService.del(path);

            existingImg.setImgUrl(imageDTO.getImgUrl());
            existingImg.setImgName(imageDTO.getImgName());
            existingImg.setOriImgName(imageDTO.getOriImgName());
            
            imageDTO = modelMapper.map(existingImg, ImageDTO.class);
            log.info("기존 대표이미지 업데이트 완료");
        } else {
            // 새로운 이미지 저장
            image.setRepImgYn(repImgYn);
            image.setStudy(study);

            image = imageRepository.save(image);
            imageDTO = modelMapper.map(image, ImageDTO.class);
            log.info("새로운 이미지 저장 완료 - 이미지 ID: {}", image.getId());
        }

        return imageDTO;
    }
}
