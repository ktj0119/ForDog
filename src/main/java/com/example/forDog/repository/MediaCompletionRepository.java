package com.example.forDog.repository;

import com.example.forDog.entity.MediaCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaCompletionRepository extends JpaRepository<MediaCompletion, Integer> {

    Optional<MediaCompletion> findByMember_No(int memberNo);

    Optional<MediaCompletion> findByMember_NoAndMediaGroup_No(int memberNo, int mediaGroupNo);

}
