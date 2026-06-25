package com.bookkok.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SecurityUtil {
	// Filter를 통과한 이후 API에서 Username을 가져올 수 있도록 하는 코드

    private SecurityUtil() {}

    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("SecurityUtil: " + authentication);
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        log.info(authentication.getName());
        return authentication.getName();
    }
}
