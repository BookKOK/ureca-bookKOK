package com.bookkok.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookkok.member.entity.Member;
import com.bookkok.member.entity.RefreshToken;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
	

    Optional<RefreshToken> findByMember(Member member);
    
    void deleteByMember_MemberId(String memberId);
}