package com.example.forDog.service;

import com.example.forDog.dto.MediaGroupDTO;
import com.example.forDog.entity.MediaGroup;
import com.example.forDog.repository.MediaGroupRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaGroupService {

    private final MediaGroupRepository repository;
    private final ModelMapper mapper;

    public List<MediaGroupDTO> getSelectAll() {
        List<MediaGroup> entityList = repository.findAll();
        List<MediaGroupDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaGroupDTO.class));
        }
        return dtoList;
    }

    public Page<MediaGroupDTO> getSelectAll(int page, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Order.asc("no")));
        Page<MediaGroup> pageList;

        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        if (!existKeyword) {
            pageList = repository.findAll(pageable);
        } else {
            pageList = repository.findByNameContaining(searchKeyword, pageable);
        }

        return pageList.map(mediaGroup -> mapper.map(mediaGroup, MediaGroupDTO.class));
    }

    // 미디어 비디오가 1개이상, 퀴즈 갯수(quizNumber) 이상 저장된 그룹만 조회
    public Page<MediaGroupDTO> getSelectFilterAll(int quizNumber, int page, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Order.asc("no")));
        Page<MediaGroup> pageList = repository.findByFilterAll(quizNumber, searchKeyword, pageable);

        for (MediaGroup mediaGroup : pageList.getContent()) {
            mediaGroup.setIsActive(true);
            setUpdate(mapper.map(mediaGroup, MediaGroupDTO.class));
        }
        return pageList.map(mediaGroup -> mapper.map(mediaGroup, MediaGroupDTO.class));
    }

    public MediaGroupDTO getSelectOne(int no) {
        Optional<MediaGroup> om = repository.findById(no);

        if (om.isEmpty()) {
            return null;
        }

        MediaGroup mediaGroup = om.get();
        return mapper.map(mediaGroup, MediaGroupDTO.class);
    }


    public MediaGroupDTO getSelectOne(MediaGroupDTO mediaGroupDTO) {
        Optional<MediaGroup> om = repository.findById(mediaGroupDTO.getNo());

        if (om.isEmpty()) {
            return null;
        }

        MediaGroup mediaGroup = om.get();
        return mapper.map(mediaGroup, MediaGroupDTO.class);
    }

    public void setInsert(MediaGroupDTO mediaGroupDTO) {
        repository.save(mapper.map(mediaGroupDTO, MediaGroup.class));
    }

    public void setUpdate(MediaGroupDTO mediaGroupDTO) {
        repository.save(mapper.map(mediaGroupDTO, MediaGroup.class));
    }

    public void setDelete(MediaGroupDTO mediaGroupDTO) {
        repository.delete(mapper.map(mediaGroupDTO, MediaGroup.class));
    }

}
