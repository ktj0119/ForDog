package com.example.forDog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDataDto {
    private int playerLives;
    private String currentSceneName;
    private int saveSlot;
    private String saveTime;
    private String userId; // memberNo로 조회한 loggedInUserId를 담을 변수라 String
}