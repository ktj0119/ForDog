package com.example.forDog.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReadingDTO {

    private int no;

    private String readingGroup;

    private String subject;

    private String content;

    private String attachment;

    private LocalDateTime regiDate;

    // 파일 업로드 및 다운로드를 위해 추가(TJ)
    private MultipartFile file;

}
