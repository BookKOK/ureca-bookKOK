package com.bookkok.admin.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookkok.admin.repository.AdminUserRepository;
import com.bookkok.member.entity.Member;
import com.bookkok.member.entity.RoleType;
import com.bookkok.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 권한 검증을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthorizationService {

    /**
     * 로그인한 회원의 권한을 확인하기 위해 회원 정보를 조회하는 Repository
     */
    private final AdminUserRepository adminUserRepository;

    /**
     * 현재 로그인한 사용자가 관리자인지 검증
     */
    public void validateAdmin() {

        // 1. SecurityContext에서 현재 로그인한 회원 ID 조회
        String memberId = SecurityUtil.getCurrentUsername();

        // 2. 회원 ID로 회원 정보 조회
        Member member = adminUserRepository.findById(memberId)
                .orElseThrow(() -> new AccessDeniedException("로그인한 회원 정보를 찾을 수 없습니다."));

        // 3. 회원 권한이 ADMIN인지 확인
        if (member.getRoleName() != RoleType.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
    }
}
