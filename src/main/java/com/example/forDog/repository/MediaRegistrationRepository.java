package com.example.forDog.repository;

import com.example.forDog.entity.MediaRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaRegistrationRepository extends JpaRepository<MediaRegistration, Integer> {

    List<MediaRegistration> findAllByMember_No(int memberNo);

    Optional<MediaRegistration> findByMember_NoAndMediaGroup_No(int memberNo, int mediaGroupNo);

}
