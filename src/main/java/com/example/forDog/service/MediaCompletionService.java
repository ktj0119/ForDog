package com.example.forDog.service;


import com.example.forDog.dto.MediaCompletionDTO;
import com.example.forDog.dto.MemberDTO;
import com.example.forDog.entity.MediaCompletion;
import com.example.forDog.repository.MediaCompletionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaCompletionService {

    private final MediaCompletionRepository repository;
    private final ModelMapper mapper;

    public List<MediaCompletionDTO> getSelectAll() {
        List<MediaCompletion> entityList = repository.findAll();
        List<MediaCompletionDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaCompletionDTO.class));
        }
        return dtoList;
    }

    // member_no로 조회
    public List<MediaCompletionDTO> getSelectInMember(MemberDTO memberDTO) {
        List<MediaCompletion> entityList = new ArrayList<>();
        List<MediaCompletionDTO> dtoList = new ArrayList<>();

        Optional<MediaCompletion> om = repository.findByMember_No(memberDTO.getNo());
        if (om.isPresent()) {
            MediaCompletion mediaCompletion = om.get();
            entityList.add(mediaCompletion);
            for (int i = 0; i < entityList.size(); i++) {
                dtoList.add(mapper.map(entityList.get(i), MediaCompletionDTO.class));
            }
        }
        return dtoList;
    }

    public MediaCompletionDTO getSelectOne(MediaCompletionDTO mediaCompletionDTO) {
        Optional<MediaCompletion> om = repository.findById(mediaCompletionDTO.getNo());

        if (om.isEmpty()) {
            return null;
        }

        MediaCompletion mediaCompletion = om.get();
        return mapper.map(mediaCompletion, MediaCompletionDTO.class);
    }

    // 멤버 번호와 그룹 번호 참조하여 조회
    public MediaCompletionDTO getSelectOneInMemberAndGroup(int memberNo, int groupNo) {
        Optional<MediaCompletion> om = repository.findByMember_NoAndMediaGroup_No(
                memberNo, groupNo
        );

        if (om.isEmpty()) {
            return null;
        }

        MediaCompletion mediaCompletion = om.get();
        return mapper.map(mediaCompletion, MediaCompletionDTO.class);
    }


    public void setInsert(MediaCompletionDTO mediaCompletionDTO) {
        repository.save(mapper.map(mediaCompletionDTO, MediaCompletion.class));
    }

    public void setUpdate(MediaCompletionDTO mediaCompletionDTO) {
        repository.save(mapper.map(mediaCompletionDTO, MediaCompletion.class));
    }

    public void setDelete(MediaCompletionDTO mediaCompletionDTO) {
        repository.delete(mapper.map(mediaCompletionDTO, MediaCompletion.class));
    }

}
