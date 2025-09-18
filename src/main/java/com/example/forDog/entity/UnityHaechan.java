package com.example.forDog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(value = {AuditingEntityListener.class})
public class UnityHaechan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int no;

    @ManyToOne
    private Member member;

    public String nickname;
    public int selectedPetIndex;
    public int currentDay;
    public int currentHour;
    public int loyalty;
    public int happiness;
    public int hunger;
    public int unlockedBooks;
    public int timesFedToday;

    // --- 퀘스트 진행 상황 ---
    public Boolean quest_PottyTraining;
    public Boolean quest_CushionComfort;
    public Boolean quest_SitAndStay;
    public Boolean quest_TouchTraining;
    public Boolean quest_FirstWalk;
    public Boolean quest_FirstWash;
    public Boolean quest_PlayAlone;
    public Boolean quest_PlayDead;

    // --- 훈련 습득 정보 ---
    public Boolean learnedSit;
    public Boolean learnedPaw;
    public Boolean learnedStay;
    public Boolean learnedTouch;
    public Boolean learnedPlayDead;

    // --- 앨범 및 추억 정보 ---
    public Boolean milestoneFirstWalk;
    public Boolean milestoneLearnedAllTricks;
    public Boolean mem_FirstPet;
    public int mem_FirstPet_Day;
    public int mem_FirstPet_Hour;
    public Boolean mem_FirstMeal;
    public int mem_FirstMeal_Day;
    public int mem_FirstMeal_Hour;
    public Boolean mem_FirstPlay;
    public int mem_FirstPlay_Day;
    public int mem_FirstPlay_Hour;
    public Boolean mem_FirstWalk;
    public int mem_FirstWalk_Day;
    public int mem_FirstWalk_Hour;
    public Boolean mem_FirstSit;
    public int mem_FirstSit_Day;
    public int mem_FirstSit_Hour;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regiDate;

}

