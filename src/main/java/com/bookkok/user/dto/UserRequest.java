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
public class UserRequest {
	
	private String memberId;
	private Club clubId;
	private RoleType roleName;
	private String name;
	private String password;
	private String email;
	private String phoneNumber;
	private LocalDateTime regDate;
	
	public User toUser() {
		return User.builder()	.memberId(memberId)
								.clubId(clubId)
								.roleName(roleName)
								.name(name)
								.password(password)
								.email(email)
								.phoneNumber(phoneNumber)
								.regDate(regDate)
								.build();
	}

}
