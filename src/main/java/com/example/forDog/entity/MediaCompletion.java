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
public class MediaCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @ManyToOne
    private Member member;

    @ManyToOne
    private MediaGroup mediaGroup;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime statusDate;

}
