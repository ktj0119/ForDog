package com.example.forDog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MediaVideoProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @ManyToOne
    private Member member;

    @ManyToOne
    private MediaVideo mediaVideo;

    @Column(nullable = false)
    private int playTime;

}
