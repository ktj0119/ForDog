package com.example.forDog.repository;

import com.example.forDog.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Page<Member> findAll(Pageable pageable);

    Page<Member> findByIdContaining(String idKeyword, Pageable pageable);

    Page<Member> findByNameContaining(String nameKeyword, Pageable pageable);

    Page<Member> findByPhoneContaining(String keyword, Pageable pageable);

    Page<Member> findByIdContainingOrNameContainingOrPhoneContaining(
            String idKeyword, String nameKeyword,String phoneKeyword,Pageable pageable);

    Optional<Member> findById(String id);

    boolean existsById(String id);

    long count();

}
