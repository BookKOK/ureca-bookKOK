package com.bookkok.user.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.bookkok.club.entity.Club;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@Nonnull
	@Column(name="member_id", updatable = false, unique = true)
	private String memberId;
	
	@ManyToOne
	@JoinColumn(name = "club_id")
	private Club clubId;
	
	@Enumerated(EnumType.STRING)
	@Column(name="role_name")
	private RoleType roleName;
	
	private String name;
	private String password;
	private String email;
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	// 로그인 방식
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private Provider provider = Provider.LOCAL; // LOCAL, KAKAO
	
	@Column(name="provider_id")
	private String providerId;
	
	@CreationTimestamp
	@Column(name="reg_date", updatable = false)
	@Nonnull
	private LocalDateTime regDate;

	@Column(name = "is_blocked", nullable = false)
	@Builder.Default
	private Boolean blocked = false;
	//차단
	public void block() {
		this.blocked = true;
	}
	//차단해제
	public void unblock() {
		this.blocked = false;
	}
	
}
