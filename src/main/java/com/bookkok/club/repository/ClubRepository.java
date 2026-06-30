package com.bookkok.club.repository;

import com.bookkok.club.entity.Club;
import com.bookkok.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    //단체 개설 시 이름 중복 검사용
    boolean existsByClubName(String clubName);

    //단체장 아이디로 단체 검색
    Optional<Club> findByLeaderMemberId(String leaderName);

    //단체 이름으로 단체 검색
    List<Club> findByClubNameContaining(String keyword);

    //단체 삭제 시 확인용
    void deleteByClubId(Long clubId);

    //단체 회원 목록 조회
    @Query("SELECT m FROM Member m WHERE m.club.clubId = :clubId")
    List<Member> findMembersByClubId(@Param("clubId") Long clubId);

    //단체장 위임, 회원 강퇴 등을 위한 특정 회원 조회 (Member 엔티티 기준)
    @Query("SELECT m FROM Member m WHERE m.memberId = :memberId")
    Optional<Member> findMemberByMemberId(@Param("memberId") String memberId);


}
