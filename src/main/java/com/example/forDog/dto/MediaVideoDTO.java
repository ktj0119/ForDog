package com.example.forDog.dto;

import com.example.forDog.entity.MediaVideoProcess;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MediaVideoDTO {

    private int no;

    private MediaGroupDTO mediaGroup;

    private String subject;

    private String content;

    private String url;

    private int mediaLength;

    private LocalDateTime regiDate;

    private int mediaGroup_no;

    private List<MediaVideoProcess> mediaVideoProcessList;

    // 파일 관리용 추가(TJ)
    private MultipartFile file;

}
