package com.bookkok.member.dto;

import java.time.LocalDateTime;

import javax.management.relation.Role;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.bookkok.member.entity.RoleType;
import com.bookkok.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberDto {

	@Getter
	@NoArgsConstructor
	public static class SignupRequest { 			// 회원가입

	    private String memberId;
	    private String password;
	    private String passwordCheck;
	    private String name;
	    private String email;
	    private String phoneNumber;

	}
	
	@Getter
	@NoArgsConstructor
	public static class LoginRequest { 				// 로그인

	    private String memberId;
	    private String password;
	    
	    public UsernamePasswordAuthenticationToken toAuthentication() { 
	    	// 클라이언트로부터 받은 정보를 기반으로 인증 토큰을 생성하는 메서드
	        return new UsernamePasswordAuthenticationToken(memberId, password);
	    }
	}
	
	@Builder
	@Getter
	@AllArgsConstructor
	public static class ProfileResponse {			// 회원 정보 조회
		
	    private String memberId;
	    private String name;
	    private String email;
	    private String phoneNumber;
	    private RoleType roleName;
	    private LocalDateTime regDate;
	    private String clubName;


	    public static ProfileResponse from(Member user) {
	        return ProfileResponse.builder()
	                .memberId(user.getMemberId())
	                .name(user.getName())
	                .email(user.getEmail())
	                .phoneNumber(user.getPhoneNumber())
	                .clubName(
	                        user.getClub() != null
	                            ? user.getClub().getClubName()
	                            : null
	                    )
	                .roleName(user.getRoleName())
	                .regDate(user.getRegDate())
	                .build();
	    }
	}
	
	@Getter
	@NoArgsConstructor
	public static class UpdatePasswordRequest {			// 비밀번호 변경

		private String memberId;
	    private String currentPassword;
	    private String newPassword;
	    private String newPasswordCheck;
	}
	
	@Getter
	@Setter
	public static class UpdateProfileRequest {

	    private String name;
	    private String email;
	    private String phoneNumber;
	}
	
	@Getter
	@Builder
	public static class UpdateProfileResponse {

	    private String memberId;
	    private String name;
	    private String email;
	    private String phoneNumber;
	}
	
//	
//	
//	@Getter
//	@NoArgsConstructor
//	public static class WithdrawRequest {				// 회원 탈퇴
//
//	    private String password;
//	}

	
	
}
