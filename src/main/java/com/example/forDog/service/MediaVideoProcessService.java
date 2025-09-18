package com.example.forDog.service;

import com.example.forDog.dto.MediaVideoDTO;
import com.example.forDog.dto.MediaVideoProcessDTO;
import com.example.forDog.entity.MediaVideoProcess;
import com.example.forDog.repository.MediaVideoProcessRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaVideoProcessService {

    private final MediaVideoProcessRepository repository;
    private final ModelMapper mapper;

    public List<MediaVideoProcessDTO> getSelectAll() {
        List<MediaVideoProcess> entityList = repository.findAll();
        List<MediaVideoProcessDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaVideoProcessDTO.class));
        }
        return dtoList;
    }

    // 사용자별 그룹별 영상 진행도 전체 조회
    public List<MediaVideoProcessDTO> getSelectAllInMemberAndGroup(int memberNo, int mediaGroupNo) {
        List<MediaVideoProcess> entityList = repository.findByMember_NoAndMediaVideo_MediaGroup_No(memberNo, mediaGroupNo);
        List<MediaVideoProcessDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaVideoProcessDTO.class));
        }
        return dtoList;
    }

    public MediaVideoProcessDTO getSelectOne(MediaVideoProcessDTO mediaVideoProcessDTO) {
        Optional<MediaVideoProcess> om = repository.findById(mediaVideoProcessDTO.getNo());

        if (om.isEmpty()) {
            return null;
        }

        MediaVideoProcess mediaVideoProcess = om.get();
        return mapper.map(mediaVideoProcess, MediaVideoProcessDTO.class);
    }

    // 멤버 번호와 비디오 번호 참조하여 조회
    public MediaVideoProcessDTO getSelectOneInMemberAndVideo(MediaVideoProcessDTO mediaVideoProcessDTO) {
        Optional<MediaVideoProcess> om = repository.findByMember_NoAndMediaVideo_No(
                mediaVideoProcessDTO.getMember().getNo(), mediaVideoProcessDTO.getMediaVideo().getNo()
        );

        if (om.isEmpty()) {
            return null;
        }

        MediaVideoProcess mediaVideoProcess = om.get();
        return mapper.map(mediaVideoProcess, MediaVideoProcessDTO.class);
    }

    public void setInsert(MediaVideoProcessDTO mediaVideoProcessDTO) {
        repository.save(mapper.map(mediaVideoProcessDTO, MediaVideoProcess.class));
    }

    public void setUpdate(MediaVideoProcessDTO mediaVideoProcessDTO) {
        repository.save(mapper.map(mediaVideoProcessDTO, MediaVideoProcess.class));
    }

    public void setDelete(MediaVideoProcessDTO mediaVideoProcessDTO) {
        repository.delete(mapper.map(mediaVideoProcessDTO, MediaVideoProcess.class));
    }

    // 멤버 번호와 비디오 번호 참조하여 조회
    public MediaVideoProcessDTO getSelectOneInMemberAndVideo(int memberNo, int videoNo) {
        Optional<MediaVideoProcess> om = repository.findByMember_NoAndMediaVideo_No(
                memberNo, videoNo
        );

        if (om.isEmpty()) {
            return null;
        }

        MediaVideoProcess mediaVideoProcess = om.get();
        return mapper.map(mediaVideoProcess, MediaVideoProcessDTO.class);
    }

    // 영상 진행도와 이수율 계산
    public int completionRateCalculator(List<MediaVideoDTO> mediaVideoList, List<MediaVideoProcessDTO> mediaVideoProcessList) {
        if (mediaVideoProcessList.isEmpty()) {
            return 0;
        }

        int totalLength = 0;
        for (MediaVideoDTO video : mediaVideoList) {
            totalLength += video.getMediaLength();
        }

        if (totalLength <= 0) {
            return 0;
        }

        int totalPlayed = 0;
        for (MediaVideoProcessDTO process : mediaVideoProcessList) {
            totalPlayed += process.getPlayTime();
        }

        double completionRate  = (totalPlayed * 100.0) / totalLength;

        return (int) Math.round(completionRate);
    }


}
