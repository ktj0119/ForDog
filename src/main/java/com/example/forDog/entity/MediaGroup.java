package com.example.forDog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class MediaGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isActive;

    @OneToMany(mappedBy = "mediaGroup", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MediaVideo> mediaVideoList;

    @OneToMany(mappedBy = "mediaGroup", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MediaQuiz> mediaQuizList;

    @OneToMany(mappedBy = "mediaGroup", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MediaRegistration> mediaRegistrationList;

    @OneToMany(mappedBy = "mediaGroup", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MediaCompletion> mediaCompletionList;

}
