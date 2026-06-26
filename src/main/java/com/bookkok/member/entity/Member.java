package com.bookkok.member.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class Member implements Persistable<String> {

	@Id
	@Nonnull
	@Column(name="member_id", updatable = false, unique = true)
	private String memberId;
	
	@ManyToOne
	@JoinColumn(name = "club_id")
	private Club club;
	
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
	private LocalDateTime regDate;
	
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;

	public void deleteMember() {
	    this.isDeleted = true;
	}

	@Override
	public @Nullable String getId() {
		// TODO Auto-generated method stub
		return memberId;
	}

	@Override
	public boolean isNew() {
		return regDate == null;
	}
	
	public void changePassword(String password) {
	    this.password = password;
	}

	
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities(){
//		return Collections.singleton(
//	            new SimpleGrantedAuthority("ROLE_" + roleName.name())
//	    );
//	}
//	
//	@Override
//	public String getUsername() {
//		return this.memberId;
//	}
	
}
