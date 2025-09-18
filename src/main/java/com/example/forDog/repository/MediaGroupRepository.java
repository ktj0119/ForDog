package com.example.forDog.repository;

import com.example.forDog.entity.MediaGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MediaGroupRepository extends JpaRepository<MediaGroup, Integer> {

    Page<MediaGroup> findAll(Pageable pageable);

    Page<MediaGroup> findByNameContaining(
            String nameKeyword, Pageable pageable
    );

    @Query("SELECT mg FROM MediaGroup mg " +
            "LEFT JOIN mg.mediaVideoList v " +
            "LEFT JOIN mg.mediaQuizList q " +
            "WHERE (:searchKeyword IS NULL OR :searchKeyword = '' OR mg.name LIKE CONCAT('%', :searchKeyword, '%')) " +
            "GROUP BY mg " +
            "HAVING COUNT(DISTINCT v.no) > 0 AND COUNT(DISTINCT q.no) >= :quizNumber")
    Page<MediaGroup> findByFilterAll(
            @Param("quizNumber") int quizNumber,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable);

}
