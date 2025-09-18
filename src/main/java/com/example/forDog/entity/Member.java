package com.example.forDog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(value = {AuditingEntityListener.class})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Date birthDate;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String addressMain;

    private String addressDetail;

    @Column(nullable = false)
    private int admin;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime regiDate;

    // 멤버 삭제시 참조 데이터 전부 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<MediaRegistration> mediaRegistrationList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<MediaVideoProcess> mediaVideoProcessList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<MediaCompletion> mediaCompletionList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<UnityHaechan> unityHaechanList;

    // [다희] 수정 시작 부분
    @Lob
    @Column(columnDefinition = "TEXT")
    private String gameSaveData;
    // [다희] 수정 종료 부분

}
