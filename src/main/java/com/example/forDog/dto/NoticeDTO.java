package com.example.forDog.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeDTO {

    private int no;

    private String writer;

    private String subject;

    private String content;

    private int count;

    private LocalDateTime regiDate;

}
