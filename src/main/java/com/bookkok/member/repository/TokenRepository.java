package com.bookkok.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.bookkok.member.entity.Member;
import com.bookkok.member.entity.RefreshToken;

import jakarta.transaction.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
	

    Optional<RefreshToken> findByMember(Member member);
    
    @Modifying
    @Transactional
    void deleteByMember_MemberId(String memberId);
}