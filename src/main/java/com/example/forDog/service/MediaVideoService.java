package com.example.forDog.service;

import com.example.forDog.dto.MediaGroupDTO;
import com.example.forDog.dto.MediaVideoDTO;
import com.example.forDog.entity.MediaGroup;
import com.example.forDog.entity.MediaVideo;
import com.example.forDog.repository.MediaGroupRepository;
import com.example.forDog.repository.MediaVideoRepository;
import lombok.RequiredArgsConstructor;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaVideoService {

    private final MediaVideoRepository repository;
    private final MediaGroupRepository mgRepository;
    private final ModelMapper mapper;

    public List<MediaVideoDTO> getSelectAll() {
        List<MediaVideo> entityList = repository.findAll();
        List<MediaVideoDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaVideoDTO.class));
        }

        return dtoList;
    }

    public Page<MediaVideoDTO> getSelectAll(int page, String searchType, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.asc("no")));
        Page<MediaVideo> pageList;

        boolean existType = searchType != null && !searchType.isEmpty();
        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        Integer groupNo = null;
        if (existType) {
            try {
                groupNo = Integer.parseInt(searchType);
            } catch (NumberFormatException e) {
                groupNo = null;
            }
        }

        if (groupNo == null && !existKeyword) {
            pageList = repository.findAll(pageable);
        } else if (groupNo != null && !existKeyword) {
            pageList = repository.findByMediaGroup_No(groupNo, pageable);
        } else if (groupNo == null && existKeyword) {
            pageList = repository.findBySubjectContaining(searchKeyword, pageable);
        } else {
            pageList = repository.findByMediaGroup_NoAndSubjectContaining(groupNo, searchKeyword, pageable);
        }

        return pageList.map(mediaVideo -> mapper.map(mediaVideo, MediaVideoDTO.class));
    }

    // 그룹별 영상 전체 조회
    public List<MediaVideoDTO> getSelectAllInGroup(int mediaGroupNo) {
        List<MediaVideo> entityList = repository.findAllByMediaGroup_No(mediaGroupNo);
        List<MediaVideoDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaVideoDTO.class));
        }
        return dtoList;
    }


    public MediaVideoDTO getSelectOne(MediaVideoDTO mediaVideoDTO) {
        Optional<MediaVideo> om = repository.findById(mediaVideoDTO.getNo());

        if (om.isEmpty()) {
            return null;
        }

        MediaVideo mediaVideo = om.get();
        return mapper.map(mediaVideo, MediaVideoDTO.class);
    }

    // application.properties에 설정된 경로(TJ)
    @Value("${file.media.dir}")
    private String uploadDir;

    // 0813 수정(TJ)
    public void setInsert(MediaVideoDTO mediaVideoDTO) {

        // 참조값(그룹)이 있는지 확인 후에 DTO에 set
        Optional<MediaGroup> om = mgRepository.findById(mediaVideoDTO.getMediaGroup().getNo());
        if (om.isPresent()) {
            MediaGroup mediaGroup = om.get();
            MediaGroupDTO mediaGroupDTO = mapper.map(mediaGroup, MediaGroupDTO.class);
            mediaVideoDTO.setMediaGroup(mediaGroupDTO);
        }

        // 0818 파일 업로드 추가(TJ)

        MultipartFile file = mediaVideoDTO.getFile();
        File tempFile = null;
        try {
            if (file != null && !file.isEmpty()) {
                String subDir = mediaVideoDTO.getMediaGroup().getName();
                String savedFilename = storeFile(file, subDir);
                mediaVideoDTO.setUrl(savedFilename);

                // 비디오 길이 계산을 위한 임시 파일 생성
                tempFile = File.createTempFile("upload_", ".tmp");
                file.transferTo(tempFile); // MultipartFile → File 변환

                // 동영상 길이 계산
                int videoLength = calcVideoLength(tempFile);
                mediaVideoDTO.setMediaLength(videoLength);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 또는 처리 실패", e);
        } finally {
            // 임시 파일 삭제
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        repository.save(mapper.map(mediaVideoDTO, MediaVideo.class));
    }

    public void setUpdate(MediaVideoDTO mediaVideoDTO) {
        MultipartFile newFile = mediaVideoDTO.getFile();
        File tempFile = null;
        try {

            if (newFile != null && !newFile.isEmpty()) {

                // 기존 파일 삭제
                String oldFile = mediaVideoDTO.getUrl();
                String groupName = "";
                int mediaGroupNo = mediaVideoDTO.getMediaGroup().getNo();
                if (mediaGroupNo > 0) {
                    Optional<MediaGroup> groupOptional = mgRepository.findById(mediaGroupNo);
                    if (groupOptional.isPresent()) {
                        MediaGroup group = groupOptional.get();
                        if (group.getName() != null && !group.getName().isBlank()) {
                            groupName = group.getName();
                        }
                    }
                }
                if (oldFile != null && !oldFile.isBlank()) {
                    Path oldPath = Paths.get(uploadDir, groupName).resolve(oldFile).normalize();
                    Files.deleteIfExists(oldPath);
                }

                // mediaGroup.no만 들어온 상태
                int groupNo = mediaVideoDTO.getMediaGroup().getNo();

                // DB에서 name 조회
                MediaGroup group = mgRepository.findById(groupNo)
                        .orElseThrow(() -> new RuntimeException("MediaGroup not found"));

                groupName = group.getName();
                String subDir = groupName;
                if (groupName == null || groupName.isBlank()) {
                    subDir = "default";
                }

                // 파일 저장(DB에는 파일명만 저장)
                String savedFilename = storeFile(newFile, subDir);
                mediaVideoDTO.setUrl(savedFilename);
                tempFile = File.createTempFile("upload_", ".tmp");
                newFile.transferTo(tempFile);

                int videoLength = calcVideoLength(tempFile);
                mediaVideoDTO.setMediaLength(videoLength);
            }

            repository.save(mapper.map(mediaVideoDTO, MediaVideo.class));

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        } finally {
            // 임시 파일 삭제
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public void setDelete(MediaVideoDTO mediaVideoDTO) {

        try {
            String storedFile = mediaVideoDTO.getUrl();

            String groupName = "";
            int mediaGroupNo = mediaVideoDTO.getMediaGroup().getNo();
            if(mediaGroupNo > 0) {
                Optional<MediaGroup> groupOptional = mgRepository.findById(mediaGroupNo);
                if (groupOptional.isPresent()) {
                    MediaGroup group = groupOptional.get();
                    if (group.getName() != null && !group.getName().isBlank()) {
                        groupName = group.getName();
                    }
                }
            }


            if (storedFile != null && !storedFile.isBlank()) {
                Path filePath = Paths.get(uploadDir, groupName, storedFile).normalize();
                System.out.println("Trying to delete file at: " + filePath);

                if (Files.exists(filePath)) {
                    System.out.println("File exists, deleting...");
                    Files.delete(filePath);
                } else {
                    System.out.println("File not found at: " + filePath);
                }
            }

            repository.delete(mapper.map(mediaVideoDTO, MediaVideo.class));

        } catch (Exception e) {
            e.printStackTrace(); // 로그 확인 중요
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

    // DTO 복사
    public MediaVideoDTO copyDTO(MediaVideoDTO mediaVideoDTO) {
        return mapper.map(mediaVideoDTO, MediaVideoDTO.class);
    }

    // 파일 저장 메서드
    public String storeFile(MultipartFile file, String subDirName) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String originalFilename = file.getOriginalFilename();
        String datePrefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String newFilename = datePrefix + "_" + originalFilename;

        // 하위 디렉토리 경로 포함
        Path dirPath = Paths.get(uploadDir, subDirName).toAbsolutePath().normalize();
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path filePath = dirPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // DB에 저장되는 파일명은 하위 경로가 포함하지 않게 하기
        return newFilename;
    }

    // 파일 다운로드
    public Resource loadFileAsResource(String filename) throws MalformedURLException {
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        return new UrlResource(filePath.toUri());
    }

    // 0819 동영상 길이 계산 메소드 추가(TJ)
    public int calcVideoLength(File videoFile) {
        try (FileChannelWrapper ch = NIOUtils.readableFileChannel(videoFile.getAbsolutePath())) {
            FrameGrab grab = FrameGrab.createFrameGrab(ch);
            double duration = grab.getVideoTrack().getMeta().getTotalDuration();
            return (int) duration;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("동영상 길이 분석 실패", e);
        }
    }

    public long countVideo() {
        return repository.count();
    }
}
