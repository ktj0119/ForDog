package com.example.forDog.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MediaCompletionDTO {

    private int no;

    private MemberDTO member;

    private MediaGroupDTO mediaGroup;

    private LocalDateTime statusDate;

    private int member_no;

    private int mediaGroup_no;

}
