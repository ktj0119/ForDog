package com.example.forDog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(value = {AuditingEntityListener.class})
public class MediaVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @ManyToOne
    private MediaGroup mediaGroup;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    @Column(nullable = false)
    private int mediaLength;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime regiDate;

    @OneToMany(mappedBy = "mediaVideo", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MediaVideoProcess> mediaVideoProcessList;

}
