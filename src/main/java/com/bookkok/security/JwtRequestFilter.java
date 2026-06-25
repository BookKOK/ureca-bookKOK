package com.bookkok.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String BEARER_PREFIX = "Bearer";
    
    /*
     * JWT = 출입증
	 * resolveToken = 출입증 검사
	 * validateToken = 위조 여부 확인
	 * SecurityContext = “출입 허용 명단”
	 * Authentication = “이 사람 누구인지 신분증 정보”
     */

    @Override
    /**
     * SecurityContext에 Access Token으로부터 뽑아온 인증 정보를 저장
     * SecurityContext는 어디서든 접근 가능한데, 
     * 정상적으로 Filter를 통과하여 Controller에 도착한다면, 
     * SecurityContext내부에 Member의 username이 있다는 것이 보장됨.
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
    		throws ServletException, IOException {
    	
    	// HTTP 헤더에서 JWT 가져옴
        String jwt = resolveToken(request);

        
        // 토큰 값이 있고, 유효한 값인지 확인(서명/만료 체크)
        if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            // Spring Security가 이해하는 “로그인 객체”로 변환
        	Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            // 이 요청은 “로그인된 사용자”로 처리됨
        	SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Header에서 Authorization부분을 추출할 때 
     * Type이 Bearer인지 확인 후, 
     * 일치한다면 JWT부분만 추출하여 doFilter에 제공
     */
    private String resolveToken(HttpServletRequest request) {
    	// 헤더에서 Authorization 가져옴
        String token = request.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);    //"Bearer "를 뺀 값, 즉 토큰 값
        }

        return null;
    }
}