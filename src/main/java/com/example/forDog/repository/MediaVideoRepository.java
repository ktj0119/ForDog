package com.example.forDog.repository;

import com.example.forDog.entity.MediaVideo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaVideoRepository extends JpaRepository<MediaVideo, Integer> {

    Page<MediaVideo> findAll(Pageable pageable);

    List<MediaVideo> findAllByMediaGroup_No(int mediaGroupNo);

    Page<MediaVideo> findByMediaGroup_No(
            int mediaGroupNo, Pageable pageable
    );

    Page<MediaVideo> findBySubjectContaining(
            String subjectKeyword, Pageable pageable
    );

    Page<MediaVideo> findByMediaGroup_NoAndSubjectContaining(
            int mediaGroupNo, String subjectKeyword, Pageable pageable
    );

    long count();

}
