package com.example.forDog.service;

import com.example.forDog.dto.UnityHaechanDTO;
import com.example.forDog.entity.UnityHaechan;
import com.example.forDog.repository.UnityHaechanRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UnityHaechanService {

    private final UnityHaechanRepository repository;
    private final ModelMapper mapper;

    public List<UnityHaechanDTO> getSelectAll() {
        List<UnityHaechan> entityList = repository.findAll();
        List<UnityHaechanDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), UnityHaechanDTO.class));
        }
        return dtoList;
    }

    public UnityHaechanDTO getSelectOne(UnityHaechanDTO unityHaechanDTO) {
        Optional<UnityHaechan> ou = repository.findById(unityHaechanDTO.getNo());

        if (ou.isEmpty()) {
            return null;
        }

        UnityHaechan unityHaechan = ou.get();
        return mapper.map(unityHaechan, UnityHaechanDTO.class);
    }

    // 멤버 번호로 조회
    public UnityHaechanDTO getSelectOneInMember(int memberNo) {
        Optional<UnityHaechan> ou = repository.findByMember_No(memberNo);

        if (ou.isEmpty()) {
            return null;
        }

        UnityHaechan unityHaechan = ou.get();
        return mapper.map(unityHaechan, UnityHaechanDTO.class);
    }

    public void setInsert(UnityHaechanDTO unityHaechanDTO) {
        repository.save(mapper.map(unityHaechanDTO, UnityHaechan.class));
    }

    public void setUpdate(UnityHaechanDTO unityHaechanDTO) {
        repository.save(mapper.map(unityHaechanDTO, UnityHaechan.class));
    }

    public void setDelete(UnityHaechanDTO unityHaechanDTO) {
        repository.delete(mapper.map(unityHaechanDTO, UnityHaechan.class));
    }

    public List<String> getStageName() {
        return new ArrayList<>(Arrays.asList("Chapter1", "Chapter2", "Chapter3", "Chapter4"));
    }

}
