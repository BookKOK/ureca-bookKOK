package com.bookkok.security;

import java.awt.RenderingHints.Key;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bookkok.member.dto.TokenDto.TokenResponse;
import com.bookkok.member.entity.RoleType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import javax.crypto.SecretKey;

@Component
@Log4j2
public class JwtTokenProvider {

	private final SecretKey encodedKey;
    private static final String BEARER_TYPE = "Bearer";
    private static final Long accessTokenValidationTime = 30 * 60 * 1000L;   //30분
    private static final Long refreshTokenValidationTime = 7 * 24 * 60 * 60 * 1000L;  //7일

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.encodedKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * accessToken과 refreshToken을 생성함
     * @param subject
     * @return TokenDTO
     * subject는 Form Login방식의 경우 userId, Social Login방식의 경우 email
     */
    public TokenResponse createTokenDTO(String subject, RoleType role) {

        //권한 -> String
    	String authority = "ROLE_" + role.name();

        //토큰 생성시간
        Instant now = Instant.from(OffsetDateTime.now());
        
        //accessToken 만료시간
        Instant refreshTokenExpirationDate = now.plusMillis(refreshTokenValidationTime);

        //accessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(subject)
                .claim("role", authority)
                .setExpiration(Date.from(now.plusMillis(accessTokenValidationTime)))
                .signWith(encodedKey)
                .compact();

        //refreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(Date.from(now.plusMillis(refreshTokenValidationTime)))
                .signWith(encodedKey)
                .compact();

        //TokenDTO에 두 토큰을 담아서 반환
        return TokenResponse.builder()
                .tokenType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .duration(Duration.ofMillis(refreshTokenValidationTime))
                .build();
    }

    /**
     * UsernamePasswordAuthenticationToken으로 보내 인증된 유저인지 확인
     * JWT 토큰을 꺼내서 Spring Security가 이해할 수 있는 로그인 사용자로 바꿔주는 코드
     * @param accessToken
     * @return Authentication
     * @throws ExpiredJwtException
     */
    public Authentication getAuthentication(String accessToken) throws ExpiredJwtException {
        // JWT를 까서 안에 들어있는 데이터 꺼냄
    	Claims claims = Jwts.parserBuilder().setSigningKey(encodedKey)
        									.build()
        									.parseClaimsJws(accessToken)
        									.getBody();

    	// JWT 안에 있던 role 꺼냄
        String roleString = (String) claims.get("role");
        
        String pureRole = roleString.replace("ROLE_", "");
        
        if(roleString == null) {
        	throw new RuntimeException("권한정보가 없는 토큰입니다.");
        }
        
        // 문자열 "USER" → enum USER로 변환
        RoleType roleType = RoleType.valueOf(pureRole);

        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
        
        // Spring Security용 사용자 객체 만들기
        UserDetails user = new User(
        		claims.getSubject(), "", authorities);
        
        // 이 사람 로그인 되어 있음. USER 권한 있음
        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(encodedKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
