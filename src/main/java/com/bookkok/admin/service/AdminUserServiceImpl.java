package com.bookkok.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookkok.admin.dto.AdminUserDto;
import com.bookkok.admin.repository.AdminUserRepository;
import com.bookkok.admin.service.AdminUserService;
import com.bookkok.member.entity.Member;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 회원 관리 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;

    /**
     * 회원 목록 조회
     *
     * @return 회원 목록
     */
    @Override
    public List<AdminUserDto.SummaryResponse> getAllUsers() {

        // 1. 회원 목록 조회
        List<Member> users = adminUserRepository.findAll();

        // 2. DTO 변환
        List<AdminUserDto.SummaryResponse> responses = users.stream()
                .map(AdminUserDto.SummaryResponse::from)
                .toList();

        // 3. 결과 반환
        return responses;
    }

    /**
     * 회원 상세 조회
     *
     * @param memberId 회원 ID
     * @return 회원 상세 정보
     */
    @Override
    public AdminUserDto.DetailResponse getUser(String memberId) {

        // 1. 회원 조회
        Member member = adminUserRepository.findById(memberId)
                .orElseThrow();

        // TODO : 회원 조회 예외 처리 추가

        // 2. DTO 변환
        AdminUserDto.DetailResponse response =
                AdminUserDto.DetailResponse.from(member);

        // 3. 결과 반환
        return response;
    }

    /**
     * 회원 차단
     *
     * @param memberId 회원 ID
     * @return 회원 차단 결과
     */
    @Override
    @Transactional
    public AdminUserDto.BlockResponse blockUser(String memberId) {

        // 1. 회원 조회
        Member member = adminUserRepository.findById(memberId)
                .orElseThrow();

        // TODO : 회원 조회 예외 처리 추가

        // TODO : 관리자 권한 검증 추가

        // 2. 회원 차단
        member.block();

        // 3. DTO 변환
        AdminUserDto.BlockResponse response =
                AdminUserDto.BlockResponse.from(member);

        // 4. 결과 반환
        return response;
    }

    /**
     * 회원 차단 해제
     *
     * @param memberId 회원 ID
     * @return 회원 차단 해제 결과
     */
    @Override
    @Transactional
    public AdminUserDto.BlockResponse unblockUser(String memberId) {

        // 1. 회원 조회
        Member user = adminUserRepository.findById(memberId)
                .orElseThrow();

        // TODO : 회원 조회 예외 처리 추가

        // TODO : 관리자 권한 검증 추가

        // 2. 회원 차단 해제
        user.unblock();

        // 3. DTO 변환
        AdminUserDto.BlockResponse response =
                AdminUserDto.BlockResponse.from(user);

        // 4. 결과 반환
        return response;
    }
}