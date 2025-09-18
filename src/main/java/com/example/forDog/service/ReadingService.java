package com.example.forDog.service;

import com.example.forDog.dto.ReadingDTO;
import com.example.forDog.dto.ShelterDTO;
import com.example.forDog.entity.Reading;
import com.example.forDog.entity.Shelter;
import com.example.forDog.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
public class ReadingService {

    // application.properties에 설정된 경로(TJ)
    @Value("${file.reading.dir}")
    private String uploadDir;

    private final ReadingRepository repository;
    private final ModelMapper mapper;

    public List<ReadingDTO> getSelectAll() {
        List<Reading> entityList = repository.findAll();
        List<ReadingDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), ReadingDTO.class));
        }
        return dtoList;
    }

    public Page<ReadingDTO> getSelectAll(int page, String searchType, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("no")));
        Page<Reading> pageList;

        boolean existType = searchType != null && !searchType.isEmpty();
        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        if (!existType && !existKeyword) {
            pageList = repository.findAll(pageable);
        } else if (existType && !existKeyword) {
            pageList = repository.findByReadingGroupContaining(searchType, pageable);
        } else if (!existType && existKeyword) {
            pageList = repository.findBySubjectContaining(searchKeyword, pageable);
        } else {
            pageList = repository.findByReadingGroupContainingAndSubjectContaining(searchType, searchKeyword, pageable);
        }

        return pageList.map(reading -> mapper.map(reading, ReadingDTO.class));
    }

    public ReadingDTO getSelectOne(ReadingDTO readingDTO) {
        Optional<Reading> or = repository.findById(readingDTO.getNo());

        if (or.isEmpty()) {
            return null;
        }

        Reading reading = or.get();
        return mapper.map(reading, ReadingDTO.class);
    }

    public ReadingDTO getSelectPrev(int no, String searchType, String searchKeyword) {
        boolean existType = searchType != null && !searchType.isEmpty();
        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        Optional<Reading> or;

        if (!existType && !existKeyword) {
            or = repository.findTopByNoGreaterThanOrderByNoAsc(no);
        } else if (existType && !existKeyword) {
            or = repository.findTopByNoGreaterThanAndReadingGroupContainingOrderByNoAsc(no, searchType);
        } else if (!existType && existKeyword) {
            or = repository.findTopByNoGreaterThanAndSubjectContainingOrderByNoAsc(no, searchKeyword);
        } else {
            or = repository.findTopByNoGreaterThanAndReadingGroupContainingAndSubjectContainingOrderByNoAsc(no, searchType, searchKeyword);
        }

        if (or.isEmpty()) {
            return null;
        }

        Reading reading = or.get();
        return mapper.map(reading, ReadingDTO.class);
    }

    public ReadingDTO getSelectNext(int no, String searchType, String searchKeyword) {
        boolean existType = searchType != null && !searchType.isEmpty();
        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        Optional<Reading> or;

        if (!existType && !existKeyword) {
            or = repository.findTopByNoLessThanOrderByNoDesc(no);
        } else if (existType && !existKeyword) {
            or = repository.findTopByNoLessThanAndReadingGroupContainingOrderByNoDesc(no, searchType);
        } else if (!existType && existKeyword) {
            or = repository.findTopByNoLessThanAndSubjectContainingOrderByNoDesc(no, searchKeyword);
        } else {
            or = repository.findTopByNoLessThanAndReadingGroupContainingAndSubjectContainingOrderByNoDesc(no, searchType, searchKeyword);
        }

        if (or.isEmpty()) {
            return null;
        }

        Reading reading = or.get();
        return mapper.map(reading, ReadingDTO.class);
    }

    public List<String> getReadingGroup() {
        return repository.findReadingGroup();
    }

    // 0815 Insert에 첨부파일까지 처리되도록 수정(TJ)
    public void setInsert(ReadingDTO readingDTO) {
        try {
            MultipartFile file = readingDTO.getFile();
            if (file != null && !file.isEmpty()) {
                String savedFilename = storeFile(file);
                readingDTO.setAttachment(savedFilename);
            }
            repository.save(mapper.map(readingDTO, Reading.class));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    // 0818 파일도 같이 수정되게 수정(TJ)
    public void setUpdate(ReadingDTO readingDTO) {
        try {
            MultipartFile newFile = readingDTO.getFile();

            if (newFile != null && !newFile.isEmpty()) {
                // 기존 파일 삭제
                String oldFile = readingDTO.getAttachment();
                if (oldFile != null && !oldFile.isBlank()) {
                    Path oldPath = Paths.get(uploadDir, oldFile);
                    Files.deleteIfExists(oldPath);
                }

                // 새 파일 저장
                String savedFilename = storeFile(newFile);
                readingDTO.setAttachment(savedFilename);
            }

            repository.save(mapper.map(readingDTO, Reading.class));

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    // 0815 파일도 같이 삭제되게 수정(TJ)
    public void setDelete(ReadingDTO readingDTO) {
        try {
            String storedFile = readingDTO.getAttachment();
            if (storedFile != null && !storedFile.isBlank()) {
                Path filePath = Paths.get(uploadDir, storedFile);
                Files.deleteIfExists(filePath);
            }
            repository.delete(mapper.map(readingDTO, Reading.class));
        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

    // 파일 저장 메서드
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String originalFilename = file.getOriginalFilename();
        String datePrefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String newFilename = datePrefix + "_" + originalFilename;

        Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path filePath = dirPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return newFilename;
    }

    // 파일 로드 (다운로드용)
    public Resource loadFileAsResource(String filename) throws MalformedURLException {
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        return new UrlResource(filePath.toUri());
    }

    public String getFileName(String attachment) {
        if (attachment == null) {
            return "";
        }

        int index = attachment.indexOf("_");

        return attachment.substring(index + 1);
    }

    public Resource loadFile(String fileName) throws IOException {
        return new UrlResource("file:C:/ForDog/Reading/" + fileName);
    }

    public String encodeFile(String fileName) {
        String name = fileName.substring(fileName.indexOf("_") + 1);

        return URLEncoder.encode(name, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }


}
