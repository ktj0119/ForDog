package com.example.forDog.repository;

import com.example.forDog.entity.Shelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterRepository extends JpaRepository<Shelter, Integer> {

    Page<Shelter> findAll(Pageable pageable);

    Page<Shelter> findByRegionContaining(
            String regionKeyword, Pageable pageable
    );

    Page<Shelter> findByShelterNameContaining(
            String shelterNameKeyword, Pageable pageable
    );

    Page<Shelter> findByRegionContainingAndShelterNameContaining(
            String regionKeyword, String shelterNameKeyword, Pageable pageable
    );

    long count();

}
