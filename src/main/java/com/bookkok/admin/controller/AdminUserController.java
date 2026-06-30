package com.bookkok.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookkok.admin.dto.AdminUserDto;
import com.bookkok.admin.service.AdminUserService;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 회원 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 회원 목록 조회
     *
     * @return 회원 목록
     */
    @GetMapping
    public ResponseEntity<List<AdminUserDto.SummaryResponse>> getAllUsers() {

        return ResponseEntity.ok(
                adminUserService.getAllUsers());
    }

    /**
     * 회원 상세 조회
     *
     * @param memberId 회원 ID
     * @return 회원 상세 정보
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<AdminUserDto.DetailResponse> getUser(
            @PathVariable String memberId) {

        return ResponseEntity.ok(
                adminUserService.getUser(memberId));
    }

    /**
     * 회원 차단
     *
     * @param memberId 회원 ID
     * @return 회원 차단 결과
     */
    @PatchMapping("/{memberId}/block")
    public ResponseEntity<AdminUserDto.BlockResponse> blockUser(
            @PathVariable String memberId) {

        return ResponseEntity.ok(
                adminUserService.blockUser(memberId));
    }

    /**
     * 회원 차단 해제
     *
     * @param memberId 회원 ID
     * @return 회원 차단 해제 결과
     */
    @PatchMapping("/{memberId}/unblock")
    public ResponseEntity<AdminUserDto.BlockResponse> unblockUser(
            @PathVariable String memberId) {

        return ResponseEntity.ok(
                adminUserService.unblockUser(memberId));
    }

}