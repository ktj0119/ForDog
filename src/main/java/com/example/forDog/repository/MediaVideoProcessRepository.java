package com.example.forDog.repository;

import com.example.forDog.entity.MediaVideoProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaVideoProcessRepository extends JpaRepository<MediaVideoProcess, Integer> {

//  Page<MediaVideoProcess> findAll(Pageable pageable);

    //member_no로 진행도 조회(TJ)
    List<MediaVideoProcess> findByMember_no(int memberNo);

    Optional<MediaVideoProcess> findByMember_NoAndMediaVideo_No(int memberNo, int mediaVideoNo);

    List<MediaVideoProcess> findByMember_NoAndMediaVideo_MediaGroup_No(int memberNo, int mediaGroupNo);

}
