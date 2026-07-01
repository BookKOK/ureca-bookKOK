package com.bookkok.member.service;

import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookkok.member.repository.MemberRepository;
import com.bookkok.member.repository.TokenRepository;
import com.bookkok.util.PhoneNumberUtil;
import com.bookkok.member.dto.TokenDto.TokenResponse;
import com.bookkok.member.dto.MemberDto.LoginRequest;
import com.bookkok.member.dto.MemberDto.ProfileResponse;
import com.bookkok.member.dto.MemberDto.SignupRequest;
import com.bookkok.member.dto.MemberDto.UpdatePasswordRequest;
import com.bookkok.member.entity.Member;
import com.bookkok.member.entity.RoleType;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2 // lombok이 자동 logger 생성해줌
@Transactional(readOnly = true) // 이 서비스는 기본적으로 조회만 한다 를 Spring에게 전달
@RequiredArgsConstructor
public class MemberService { // implements UserDetailsService 
	
	private final MemberRepository memberRepository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthService authService;
	private final TokenService tokenService;
	


//	
//	public Member findMemberByMemberId(String memberId) {
//		return memberRepository.findById(memberId)
//				.orElseThrow(() -> new RuntimeException("해당 ID을 가진 사용자가 존재하지 않습니다."));
//	}
//	
//	// 비밀번호 찾기, 소셜 로그인 등에 사용
//	public Member findMemberByEmail(String email) {
//		Member member = memberRepository.findByEmail(email)
//				.orElseThrow(() -> new RuntimeException("해당 email을 가진 사용자가 존재하지 않습니다."));
//		return member;
//	}
//	
//	public MemberDto.ProfileResponse getMember(String memberId) {
//		Member member = findMemberByMemberId(memberId);
//		return MemberDto.ProfileResponse.from(member);
//	}
	
	/**
	 * 회원가입
	 * @param SignupRequest request
	 * @return 
	 */
	@Transactional(readOnly = false)
	public void signup(SignupRequest request) {
		if(memberRepository.existsById(request.getMemberId())) {
			throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
		}
		
		if (memberRepository.existsByEmail(request.getEmail())) {
	        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
	    }
		
		if (!request.getPassword().equals(request.getPasswordCheck())) {
	        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
	    }
		
		Member member = Member.builder()
	            .memberId(request.getMemberId())
	            .password(passwordEncoder.encode(request.getPassword()))
	            .name(request.getName())
	            .email(request.getEmail())
	            .phoneNumber(PhoneNumberUtil.normalize(request.getPhoneNumber()))
	            .roleName(RoleType.USER)
	            .build();
		
		memberRepository.save(member);
	}
	
	@Transactional
	/**
	 * 로그인
	 * @param request
	 * @return TokenResponse
	 */
	public TokenResponse login(LoginRequest request) {
		authService.authenticateLogin(request);
		
		Member member = memberRepository.findById(request.getMemberId())
				.orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));;
		
				return tokenService.createToken(member);
	}
	
	
	@Transactional
	/**
	 * 비밀번호 변경
	 * @param request
	 * @return 
	 */
	public void resetPassword(UpdatePasswordRequest request) {
		
		Member member = memberRepository.findById(request.getMemberId())
				.orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
		
		if(!passwordEncoder.matches(
				request.getCurrentPassword(),
				member.getPassword())) {
			throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
		}
		
		member.changePassword(passwordEncoder.encode(request.getNewPassword()));
	}
	
	@Transactional
	/**
	 * 회원 soft delete
	 * @param String memberId
	 * @return 
	 */
	public void deleteMember(String memberId) {
		
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
		
		member.deleteMember();
		
		tokenRepository.deleteByMember_MemberId(memberId);
	}
	
}
