package com.example.forDog.dto;

import com.example.forDog.entity.MediaCompletion;
import com.example.forDog.entity.MediaQuiz;
import com.example.forDog.entity.MediaRegistration;
import com.example.forDog.entity.MediaVideo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MediaGroupDTO {

    private int no;

    private String name;

    private String description;

    private Boolean isActive;

    private List<MediaVideo> mediaVideoList;

    private List<MediaQuiz> mediaQuizList;

    private List<MediaRegistration> mediaRegistrationList;

    private List<MediaCompletion> mediaCompletionList;

}
