package com.bookkok.club.repository;

import com.bookkok.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    // 단체 개설 시 이름 중복 검사용
    boolean existsByClubName(String clubName);

    // 단체장 아이디로 단체 검색
    Optional<Club> findByLeaderMemberId(String leaderName);

    // 단체 이름으로 단체 검색
    List<Club> findByClubNameContaining(String keyword);

}
