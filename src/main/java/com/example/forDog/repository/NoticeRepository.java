package com.example.forDog.repository;

import com.example.forDog.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    Page<Notice> findAll(Pageable pageable);

    Page<Notice> findByWriterContaining(String writerKeyword, Pageable pageable);

    Page<Notice> findBySubjectContaining(String subjectKeyword, Pageable pageable);

    Page<Notice> findByContentContaining(String contentKeyword, Pageable pageable);

    Page<Notice> findByWriterContainingOrSubjectContainingOrContentContaining(
            String writerKeyword, String subjectKeyword, String contentKeyword, Pageable pageable
    );

    // 가장 최신 공지사항 3개의 리스트만 가져오기
    List<Notice> findTop3ByOrderByRegiDateDesc();
    List<Notice> findTop5ByOrderByRegiDateDesc();

    // 이전글 다음글
    Optional<Notice> findTopByNoGreaterThanOrderByNoAsc(int no);
    Optional<Notice> findTopByNoLessThanOrderByNoDesc(int no);

    Optional<Notice> findTopByNoGreaterThanAndSubjectContainingOrderByNoAsc(int no, String subjectKeyword);
    Optional<Notice> findTopByNoLessThanAndSubjectContainingOrderByNoDesc(int no, String subjectKeyword);

}
