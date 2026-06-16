package com.bookkok.user.dto;

import java.time.LocalDateTime;

import com.bookkok.club.entity.Club;
import com.bookkok.user.entity.RoleType;
import com.bookkok.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

	private String memberId;
	private Club clubId;
	private RoleType roleName;
	private String name;
	private String password;
	private String email;
	private String phoneNumber;
	private LocalDateTime regDate;
	
	public UserResponse(User entity) {
		this.memberId = entity.getMemberId();
		this.clubId = entity.getClubId();
		this.roleName = entity.getRoleName();
		this.name = entity.getName();
		this.password = entity.getPassword();
		this.email = entity.getEmail();
		this.phoneNumber = entity.getPhoneNumber();
		this.regDate = entity.getRegDate();
		
	}
}
