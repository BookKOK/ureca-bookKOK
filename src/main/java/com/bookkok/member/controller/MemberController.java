package com.bookkok.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookkok.member.dto.TokenDto.TokenResponse;
import com.bookkok.member.dto.TokenDto.SocialLoginResponse;
import com.bookkok.member.dto.MemberDto.LoginRequest;
import com.bookkok.member.dto.MemberDto.SignupRequest;
import com.bookkok.member.dto.MemberDto.UpdatePasswordRequest;
import com.bookkok.member.repository.MemberRepository;
import com.bookkok.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody SignupRequest request){
		log.info("회원가입 API 진입");
		memberService.signup(request);
		
		return ResponseEntity.status(HttpStatus.CREATED)
		        .body("회원가입 성공");
	}
	
	@PostMapping("/password/reset")
	public ResponseEntity<Void> resetPassword(@RequestBody UpdatePasswordRequest request){
		memberService.resetPassword(request);
		
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteMember(Authentication auth){
		memberService.deleteMember(auth.getName());
		
		return ResponseEntity.ok().build();
	}

	
}
