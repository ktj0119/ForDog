package com.example.forDog.repository;

import com.example.forDog.entity.UnityHaechan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnityHaechanRepository extends JpaRepository<UnityHaechan, Integer> {

    Optional<UnityHaechan> findByMember_No(int memberNo);

}
