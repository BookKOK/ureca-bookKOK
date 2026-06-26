package com.bookkok.security;


import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	// 권한 없는 사용자를 처리하기 위함
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.warn("access denied in handler");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}