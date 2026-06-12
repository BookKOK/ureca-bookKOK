package com.bookkok.user.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.bookkok.club.entity.Club;

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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@Column(name="member_id")
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
	
	@CreationTimestamp
	@Column(name="reg_date", updatable = false)
	private LocalDateTime regDate;

	
}
