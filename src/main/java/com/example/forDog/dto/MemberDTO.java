package com.example.forDog.dto;

import com.example.forDog.entity.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MemberDTO {

    private int no;

    private String id;

    private String pwd;

    private String name;

    private String gender;

    private Date birthDate;

    private String phone;

    private String addressMain;

    private String addressDetail;

    private int admin;

    private LocalDateTime regiDate;

    private List<MediaRegistration> mediaRegistrationList;

    private List<MediaVideoProcess> mediaVideoProcessList;

    private List<MediaCompletion> mediaCompletionList;

    @JsonIgnore // 직렬 구조 문제 발생
    private List<UnityHaechan> unityHaechanList;

    private String gameSaveData;

}
