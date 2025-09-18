package com.example.forDog.service;


import com.example.forDog.dto.MediaRegistrationDTO;
import com.example.forDog.dto.MemberDTO;
import com.example.forDog.entity.MediaRegistration;
import com.example.forDog.repository.MediaRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaRegistrationService {

    private final MediaRegistrationRepository repository;
    private final ModelMapper mapper;

    public List<MediaRegistrationDTO> getSelectAll() {
        List<MediaRegistration> entityList = repository.findAll();
        List<MediaRegistrationDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaRegistrationDTO.class));
        }

        return dtoList;
    }

    // 멤버 번호로 리스트 조회
    public List<MediaRegistrationDTO> getSelectAllInMember(int memberNo) {
        List<MediaRegistration> entityList = repository.findAllByMember_No(memberNo);
        List<MediaRegistrationDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaRegistrationDTO.class));
        }

        return dtoList;
    }


    public MediaRegistrationDTO getSelectOne(MediaRegistrationDTO mediaRegistrationDTO) {
        Optional<MediaRegistration> om = repository.findById(mediaRegistrationDTO.getNo());

        if (om.isEmpty()) {
            return null;
        }

        MediaRegistration mediaRegistration = om.get();
        return mapper.map(mediaRegistration, MediaRegistrationDTO.class);
    }

    // member_no로 조회
    public List<MediaRegistrationDTO> getSelectInMember(MemberDTO memberDTO) {
        List<MediaRegistration> entityList = repository.findAllByMember_No(memberDTO.getNo());
        List<MediaRegistrationDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaRegistrationDTO.class));

        }
        return dtoList;
    }

    // 멤버 번호와 그룹 번호 참조하여 조회
    public MediaRegistrationDTO getSelectOneInMemberAndGroup(int memberNo, int groupNo) {
        Optional<MediaRegistration> om = repository.findByMember_NoAndMediaGroup_No(
                memberNo, groupNo
        );

        if (om.isEmpty()) {
            return null;
        }

        MediaRegistration mediaRegistration = om.get();
        return mapper.map(mediaRegistration, MediaRegistrationDTO.class);
    }


    public void setInsert(MediaRegistrationDTO mediaRegistrationDTO) {
        repository.save(mapper.map(mediaRegistrationDTO, MediaRegistration.class));
    }

    public void setUpdate(MediaRegistrationDTO mediaRegistrationDTO) {
        repository.save(mapper.map(mediaRegistrationDTO, MediaRegistration.class));
    }

    public void setDelete(MediaRegistrationDTO mediaRegistrationDTO) {
        repository.delete(mapper.map(mediaRegistrationDTO, MediaRegistration.class));
    }




}
