package com.example.studyport.service.boohwan;

import com.example.studyport.dto.StudyGroupDTO;
import com.example.studyport.entity.StudyGroup;
import com.example.studyport.entity.User;
import com.example.studyport.repository.StudyGroupRepository;
import com.example.studyport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * 그룹 생성
     * @param ownerUno  그룹장(로그인 사용자) ID
     * @param dto       생성 폼에서 넘어온 데이터
     * @return 저장된 그룹 PK
     */



    // 그룹 생성
    public Long createGroup(Long ownerUno, StudyGroupDTO dto) {
        User owner = userRepository.findById(ownerUno)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        StudyGroup entity = modelMapper.map(dto, StudyGroup.class);
        entity.setOwner(owner);
        entity.setIsPrivate(Boolean.TRUE.equals(dto.getIsPrivate()));
        StudyGroup saved = studyGroupRepository.save(entity);
        return saved.getGroupId();
    }

    // 내가 만든 그룹 목록
    public List<StudyGroupDTO> findMyGroups(Long ownerUno) {
        return studyGroupRepository.findByOwnerUno(ownerUno).stream()
                .map(g -> modelMapper.map(g, StudyGroupDTO.class))
                .toList();
    }
}
