package com.bookkok.admin.service;

import java.util.List;

import com.bookkok.admin.dto.AdminUserDto;

/**
 * 관리자 회원 관리 서비스
 */
public interface AdminUserService {

    /**
     * 회원 목록 조회
     *
     * @return 회원 목록
     */
    List<AdminUserDto.SummaryResponse> getAllUsers();

    /**
     * 회원 상세 조회
     *
     * @param memberId 회원 ID
     * @return 회원 상세 정보
     */
    AdminUserDto.DetailResponse getUser(String memberId);

    /**
     * 회원 차단
     *
     * @param memberId 회원 ID
     * @return 회원 차단 결과
     */
    AdminUserDto.BlockResponse blockUser(String memberId);

    /**
     * 회원 차단 해제
     *
     * @param memberId 회원 ID
     * @return 회원 차단 해제 결과
     */
    AdminUserDto.BlockResponse unblockUser(String memberId);

}