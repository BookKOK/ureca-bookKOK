package com.bookkok.member.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookkok.member.dto.KakaoTokenDto.KakaoTokenResponse;
import com.bookkok.member.dto.KakaoTokenDto.KakaoUserInfoResponse;
import com.bookkok.member.dto.TokenDto.TokenResponse;
import com.bookkok.member.service.KakaoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@RestController
@Log4j2
public class KakaoController {
	
	private final KakaoService kakaoService;
	
	@GetMapping("/login/kakao/auth-code")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestParam String code) {

		KakaoTokenResponse response = kakaoService.getAccessToken(code);
		KakaoUserInfoResponse userInfo = kakaoService.getUserInfo(response.getAccessToken());
		TokenResponse tokenResponse = kakaoService.login(userInfo);
		
//		log.info("카카오ID = {}", userInfo.getId());
//		log.info("이메일 = {}", userInfo.getKakaoAccount().getEmail());
//		log.info("이름 = {}", userInfo.getKakaoAccount().getName());
//		log.info("전화번호 = {}", userInfo.getKakaoAccount().getPhoneNumber());
//		log.info("닉네임 = {}", userInfo.getKakaoAccount().getProfile().getNickname());

		
		return ResponseEntity.status(HttpStatus.FOUND)
		        .location(URI.create("/home?accessToken=" + tokenResponse.getAccessToken()))
		        .build();
    }
}
