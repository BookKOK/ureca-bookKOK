package com.bookkok.member.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookkok.member.dto.MemberDto.LoginRequest;
import com.bookkok.member.dto.TokenDto.SocialLoginResponse;
import com.bookkok.member.dto.TokenDto.TokenResponse;
import com.bookkok.member.repository.TokenRepository;
import com.bookkok.member.service.AuthService;
import com.bookkok.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final MemberService memberService;
	private final AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<SocialLoginResponse> memberLogin(@RequestBody LoginRequest loginRequest){
		TokenResponse tokenDTO = memberService.login(loginRequest);
		ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", tokenDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(tokenDTO.getDuration())
                .path("/")
                .build();

		SocialLoginResponse tokenResponseDTO = SocialLoginResponse.builder()
                .isNewMember(false)
                .accessToken(tokenDTO.getAccessToken())
                .build();

        return ResponseEntity.ok()
        		.header("Set-Cookie", responseCookie.toString())
        		.body(tokenResponseDTO);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(Authentication authentication) {

	    String memberId = authentication.getName();

	    authService.logout(memberId);

	    return ResponseEntity.ok().build();
	}
}
