package com.example.forDog.repository;

import com.example.forDog.entity.MediaQuiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaQuizRepository extends JpaRepository<MediaQuiz, Integer> {

    Page<MediaQuiz> findAll(Pageable pageable);

    List<MediaQuiz> findAllByMediaGroup_No(int mediaGroupNo);

    Page<MediaQuiz> findByMediaGroup_No(int mediaGroupNo, Pageable pageable);

    Page<MediaQuiz> findByQuestionContaining(String questionKeyword, Pageable pageable);

    Page<MediaQuiz> findByMediaGroup_NoOrQuestionContaining(int mediaGroupNo, String questionKeyord, Pageable pageable);

}
