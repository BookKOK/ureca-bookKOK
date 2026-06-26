package com.bookkok.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookkok.member.entity.Provider;
import com.bookkok.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
	
	//아이디 중복 확인
	boolean existsByMemberId(String memberId);
	
	// 이메일 조회
	Optional<Member> findByEmail(String email);
	
	// 이메일 중복 확인
	boolean existsByEmail(String email);
	
	// 소셜 로그인
	Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
	
}
