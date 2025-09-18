package com.example.forDog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MediaQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @ManyToOne
    private MediaGroup mediaGroup;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

}
