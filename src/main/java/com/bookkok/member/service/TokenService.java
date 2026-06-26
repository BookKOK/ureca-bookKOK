package com.bookkok.member.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.bookkok.member.dto.MemberDto.ProfileResponse;
import com.bookkok.member.dto.TokenDto.TokenResponse;
import com.bookkok.member.entity.Member;
import com.bookkok.member.entity.RefreshToken;
import com.bookkok.member.repository.MemberRepository;
import com.bookkok.member.repository.TokenRepository;
import com.bookkok.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider tokenProvider;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    public TokenResponse createToken(ProfileResponse memberDTO) {
    	TokenResponse tokenDTO = tokenProvider.createTokenDTO(memberDTO.getMemberId(), memberDTO.getRoleName());
        Member member = memberRepository.findById(memberDTO.getMemberId()).orElseThrow(() -> new RuntimeException("Wrong Access (member does not exist)"));
        RefreshToken refreshToken = RefreshToken.builder()
                .member(member)
                .token(tokenDTO.getRefreshToken())
                .build();

        tokenRepository.save(refreshToken);

        return tokenDTO;
    }

    /**
     * 소셜 로그인 시 MemberDTO를 받아와서 토큰을 생성
     * @param member
     * @return tokenDTO
     */
    public TokenResponse createToken(Member member) {
    	TokenResponse tokenDTO = tokenProvider.createTokenDTO(member.getMemberId(), member.getRoleName());
        RefreshToken refreshToken = tokenRepository.findByMember(member)
                .map(token -> token.updateValue(tokenDTO.getRefreshToken()))
                .orElse(
                        RefreshToken.builder()
                                .member(member)
                                .token(tokenDTO.getRefreshToken())
                                .build()
                );
        tokenRepository.save(refreshToken);

        return tokenDTO;
    }

    
    /**
     * 일반 로그인 시 Member를 받아와서 토큰을 생성
     * @param tokenDTO
     * @return tokenDTO
     */
    public TokenResponse refresh(TokenResponse tokenDTO) {
        if(!tokenProvider.validateToken(tokenDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenDTO.getAccessToken());

        RefreshToken refreshToken = tokenRepository.findByMember(memberRepository.findById(authentication.getName()).get())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        if (!refreshToken.getToken().equals(tokenDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 일치하지 않습니다.");
        }

        Member member = memberRepository.findById(refreshToken.getMember().getMemberId()).orElseThrow(() -> new RuntimeException("존재하지 않는 계정입니다."));
        TokenResponse tokenDto = tokenProvider.createTokenDTO(member.getMemberId(), member.getRoleName());

        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        tokenRepository.save(newRefreshToken);

        return tokenDto;
    }
}