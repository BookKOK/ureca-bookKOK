package com.bookkok.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {
	
	private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private static final String[] URL_TO_PERMIT = {
            "/",
            "/css/**",
            "/js/**",
            "/images/**",
            "/ui/**",
            "/home",
            "/login",
            "/signup",
            "/reservations/**",
            "/clubs/**",
            "/board/**",
            "/mypage",
            "/admin",
            "/css/**",
            "/js/**",
            "/images/**",
            "/index.html",
            "/favicon.ico",
            "/api/members/signup",
            "/api/auth/login",
            "/login/kakao/auth-code",
            "/error"
//            "/auth/**",
//            "/v3/api-docs/**",
//            "/swagger-ui/**"
    };
    
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
        /* 1. CSRF(“세션 기반 로그인”에서만 필요한 공격 방어 기능) 끄기
    		  JWT는: 쿠키 세션 안 씀, 요청마다 토큰 들고 감 => JWT를 쓰면 CSRF 필요없음
        */
        .csrf(csrf -> csrf.disable())

        /* 2. 세션 STATELESS
         * JWT 방식: 서버는 아무것도 기억 안 함, 클라이언트가 토큰 들고 다님
         * 서버는 “기억 안 하는 상태”로 만들어야 JWT가 정상 작동
         * */
        .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // 3. 예외 처리
        .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        )

        // 4. URL 권한 설정
        .authorizeHttpRequests(auth -> auth
//        		.anyRequest().permitAll()
                .requestMatchers(URL_TO_PERMIT).permitAll()
                .anyRequest().authenticated()
        )
        ;

	    /* 5. JWT 필터 추가
	     * 1) JwtRequestFilter 실행
	     * 2) Authorization 헤더 확인
	     * 3) JWT 검증
	     * 4) 로그인 정보 생성
	     * 5) SecurityContext에 저장
	     * 6) Controller로 이동
	     * */
	    http.addFilterBefore( //Before : UsernamePasswordAuthenticationFilter 전에 실행해야 Spring Security가 인증된 사용자로 인식함
	            new JwtRequestFilter(jwtTokenProvider),
	            UsernamePasswordAuthenticationFilter.class
	    );


	    log.info("securityConfig");
        return http.build();
    }

    /**
     * 비밀번호 암호화에 사용
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    /**
     * AuthenticationManager: 스프링 시큐리티의 인증 처리
     * AuthenticationManager는 사용자 인증 시 Service와 PasswordEncoder를 내부적으로 사용해 인증과 권한 부여 프로세스를 처리
     */
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}