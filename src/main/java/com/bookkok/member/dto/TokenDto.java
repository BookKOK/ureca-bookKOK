package com.bookkok.member.dto;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class TokenDto {
	
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TokenResponse {
		private String tokenType;
		private String accessToken;
		private String refreshToken;
		private Duration duration;
	}
}
