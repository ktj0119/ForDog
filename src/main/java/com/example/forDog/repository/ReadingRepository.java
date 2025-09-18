package com.example.forDog.repository;

import com.example.forDog.entity.Reading;
import com.example.forDog.entity.Shelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReadingRepository extends JpaRepository<Reading, Integer> {

    Page<Reading> findAll(Pageable pageable);

    Page<Reading> findByReadingGroupContaining(
            String readingGroupKeyword, Pageable pageable
    );

    Page<Reading> findBySubjectContaining(
            String subjectKeyword, Pageable pageable
    );

    Page<Reading> findByReadingGroupContainingAndSubjectContaining(
            String readingGroupKeyword, String subjectKeyword, Pageable pageable
    );

    // 리딩그룹 중복없이 조회
    @Query("SELECT DISTINCT r.readingGroup FROM Reading r ORDER BY r.readingGroup ASC")
    List<String> findReadingGroup();

    // 이전글 다음글
    Optional<Reading> findTopByNoGreaterThanOrderByNoAsc(int no);
    Optional<Reading> findTopByNoLessThanOrderByNoDesc(int no);

    Optional<Reading> findTopByNoGreaterThanAndReadingGroupContainingOrderByNoAsc(int no, String readingGroupKeyword);
    Optional<Reading> findTopByNoLessThanAndReadingGroupContainingOrderByNoDesc(int no, String readingGroupKeyword);

    Optional<Reading> findTopByNoGreaterThanAndSubjectContainingOrderByNoAsc(int no, String subjectKeyword);
    Optional<Reading> findTopByNoLessThanAndSubjectContainingOrderByNoDesc(int no, String subjectKeyword);

    Optional<Reading> findTopByNoGreaterThanAndReadingGroupContainingAndSubjectContainingOrderByNoAsc(int no, String readingGroupKeyword, String subjectKeyword);
    Optional<Reading> findTopByNoLessThanAndReadingGroupContainingAndSubjectContainingOrderByNoDesc(int no, String readingGroupKeyword, String subjectKeyword);

}
