package com.example.studyport.service;

import com.example.studyport.dto.ImageDTO;
import com.example.studyport.entity.Image;
import com.example.studyport.entity.Study;
import com.example.studyport.repository.ImageRepository;
import com.example.studyport.repository.StudyRepository;
import groovy.util.logging.Log4j2;
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
    private ModelMapper modelMapper = new ModelMapper();


    @Override
    public ImageDTO register(MultipartFile multipartFile, Long study_id, String repImgYn) throws IOException {
        //사진을 물리적으로 저장 후 저장한 내용을 바탕으로 dto 만들어서 반환
        ImageDTO imageDTO =
            fileService.register(multipartFile);
        //반환값을 가지고 (imgDTO) db에 저장
        Image image = modelMapper.map(imageDTO, Image.class);

        //참조대상 찾기
        Study study =
            studyRepository.findById(study_id).orElseThrow(EntityNotFoundException::new);

        //대표이미지 일단은 xxxxx
        Image img = null;
        if (repImgYn != null) {
            img = imageRepository.findByStudy_IdAndRepImgYn(study.getId(), repImgYn);

        }
        if (img != null) {
            String path = img.getImgUrl() + img.getImgName();
            fileService.del(path);

            img.setImgUrl(imageDTO.getImgUrl());
            img.setImgName(imageDTO.getImgName());
            img.setOriImgName(imageDTO.getOriImgName());

        } else {

            image.setRepImgYn(repImgYn);
            image.setStudy(study);

            image =
                imageRepository.save(image);
            imageDTO = modelMapper.map(image, ImageDTO.class);

        }


        return imageDTO;
    }
}
