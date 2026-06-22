package com.bookkok.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookkok.user.entity.User;
import com.bookkok.user.entity.Provider;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	//아이디 중복 확인
	boolean existsByMemberId(String memberId);
	
	// 이메일 조회
	Optional<User> findByEmail(String email);
	
	// 소셜 로그인
	Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
	
}
