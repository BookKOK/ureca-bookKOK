package com.bookkok.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 생성
	@Column(name="token_id")
	private Long tokenId;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    private String token;

    @Builder
    public RefreshToken(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    public RefreshToken updateValue(String token) {
        this.token = token;
        return this;
    }
}
