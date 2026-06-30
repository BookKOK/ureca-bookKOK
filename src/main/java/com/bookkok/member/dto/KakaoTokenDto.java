package com.bookkok.member.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class KakaoTokenDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class KakaoTokenResponse {

	    @JsonProperty("access_token")
	    private String accessToken;

	    @JsonProperty("token_type")
	    private String tokenType;

	    @JsonProperty("refresh_token")
	    private String refreshToken;

	    @JsonProperty("id_token")
	    private String idToken;

	    @JsonProperty("expires_in")
	    private Long expiresIn;

	    @JsonProperty("scope")
	    private String scope;

	    @JsonProperty("refresh_token_expires_in")
	    private Long refreshTokenExpiresIn;
	}
	
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class KakaoUserInfoResponse {
		@JsonProperty("id")
	    private Long id;

	    @JsonProperty("kakao_account")
	    private KakaoAccount kakaoAccount;

	    @Getter
	    @Builder
	    @NoArgsConstructor
	    @AllArgsConstructor
	    public static class KakaoAccount {

	        @JsonProperty("email")
	        private String email;

	        @JsonProperty("name")
	        private String name;

	        @JsonProperty("phone_number")
	        private String phoneNumber;

	        @JsonProperty("profile")
	        private Profile profile;

	        @Getter
	        @Builder
	        @NoArgsConstructor
	        @AllArgsConstructor
	        public static class Profile {

	            @JsonProperty("nickname")
	            private String nickname;
	        }
	    }
	}
}
