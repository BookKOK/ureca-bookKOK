package com.bookkok.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.bookkok.member.entity.RefreshToken;
import com.bookkok.member.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {

	private final TokenRepository tokenRepository;
	
	@Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

		if (authentication == null) {
            return;
        }

        String memberId = authentication.getName();

        // DB에서 해당 회원 refresh token 삭제
        tokenRepository.deleteByMember_MemberId(memberId);
    }
}
