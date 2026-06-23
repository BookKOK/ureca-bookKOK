package com.bookkok.admin.service;

import com.bookkok.admin.dto.AdminCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 게시판 카테고리 관련 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminCategoryService {

    /**
     * 게시판 카테고리를 생성합니다.
     *
     * @param request 생성할 카테고리 정보
     * @return 생성된 카테고리 정보
     */
    public AdminCategoryDto.Response createCategory(
            AdminCategoryDto.CreateRequest request) {

        // TODO
        // 1. 카테고리 관리는 관리자만 가능하기에 권한이 Admin인지 확인
        // 2. 카테고리 이름 중복 여부 확인
        // 3. Category Entity 생성
        // 4. DB 저장
        // 5. Response DTO 생성 및 반환

        return null;
    }

    /**
     * 게시판 카테고리를 수정합니다.
     *
     * @param categoryId 수정할 카테고리 ID
     * @param request 수정할 카테고리 정보
     * @return 수정된 카테고리 정보
     */
    public AdminCategoryDto.Response updateCategory(
            Long categoryId,
            AdminCategoryDto.UpdateRequest request) {

        // TODO
        // 1. 카테고리 관리는 관리자만 가능하기에 권한이 Admin인지 확인
        // 2. categoryId로 카테고리 조회
        // 3. 존재 여부 확인
        // 4. 이름 중복 여부 확인
        // 5. 카테고리 정보 수정
        // 6. Response DTO 생성 및 반환

        return null;
    }

    /**
     * 게시판 카테고리 목록을 조회합니다.
     *
     * @return 전체 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<AdminCategoryDto.Response> getCategories() {

        // TODO
        // 1. 카테고리 관리는 관리자만 가능하기에 권한이 Admin인지 확인
        // 2. 전체 카테고리 조회
        // 3. Response DTO 리스트로 변환
        // 4. 반환

        return null;
    }

    /**
     * 게시판 카테고리를 삭제합니다.
     *
     * @param categoryId 삭제할 카테고리 ID
     */
    public void deleteCategory(Long categoryId) {
        // TODO
        // 1. 카테고리 관리는 관리자만 가능하기에 권한이 Admin인지 확인
        // 2. categoryId로 카테고리 조회
        // 3. 존재 여부 확인
        // 4. 삭제

    }
}