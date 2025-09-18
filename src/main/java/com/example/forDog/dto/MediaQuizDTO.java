package com.example.forDog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MediaQuizDTO {

    private int no;

    private MediaGroupDTO mediaGroup;

    private String question;

    private String answer;

    private int mediaGroup_no;

    private List<List<String>> answers;

    private List<Integer> quizNoList;

}
