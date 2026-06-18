package com.bookkok.user.dto;

import java.time.LocalDateTime;

import com.bookkok.user.entity.RoleType;
import com.bookkok.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

	@Getter
	@NoArgsConstructor
	public static class SignupRequest { 			// 회원가입

	    private String memberId;
	    private String password;
	    private String name;
	    private String email;
	    private String phoneNumber;

	    public User toEntity() {
	        return User.builder()
	                .memberId(memberId)
	                .password(password)
	                .name(name)
	                .email(email)
	                .phoneNumber(phoneNumber)
	                .roleName(RoleType.USER)
	                .build();
	    }
	}
	
	@Getter
	@NoArgsConstructor
	public static class LoginRequest { 				// 로그인

	    private String email;
	    private String password;
	}
	
	@Builder
	@AllArgsConstructor
	public static class ProfileResponse {			// 회원 정보 조회
		
	    private String memberId;
	    private String name;
	    private String email;
	    private String phoneNumber;
	    private RoleType roleName;
	    private LocalDateTime regDate;
	    private String clubName;


	    public static ProfileResponse from(User user) {
	        return ProfileResponse.builder()
	                .memberId(user.getMemberId())
	                .name(user.getName())
	                .email(user.getEmail())
	                .phoneNumber(user.getPhoneNumber())
	                .clubName(
	                        user.getClubId() != null
	                            ? user.getClubId().getClubName()
	                            : null
	                    )
	                .roleName(user.getRoleName())
	                .regDate(user.getRegDate())
	                .build();
	    }
	}
	
	@Getter
	@NoArgsConstructor
	public static class ChangePasswordRequest {			// 비밀번호 변경

	    private String currentPassword;
	    private String newPassword;
	}
	
	@Getter
	@NoArgsConstructor
	public static class UpdateProfileRequest {			// 닉네임 변경

	    private String name;
	}
	
	@Getter
	@NoArgsConstructor
	public static class WithdrawRequest {				// 회원 탈퇴

	    private String password;
	}
	
	@Getter
	@NoArgsConstructor
	public static class SocialLoginRequest {			// 소셜 로그인 구현

	    private String accessToken;
	}
	// 로그인
}
