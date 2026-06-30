package com.bookkok.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookkok.admin.dto.AdminCategoryDto;
import com.bookkok.admin.repository.AdminCategoryRepository;
import com.bookkok.board.entity.Category;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 게시판 카테고리 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminCategoryService {

    private final AdminCategoryRepository adminCategoryRepository;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 게시판 카테고리를 생성
     *
     * @param request 생성할 카테고리 정보
     * @return 생성된 카테고리 정보
     */
    public AdminCategoryDto.Response createCategory(
            AdminCategoryDto.CreateRequest request) {

        // 1. 카테고리 관리는 관리자만 가능하기에 권한이 Admin인지 확인
        adminAuthorizationService.validateAdmin();

        // 2. 카테고리 이름 중복 여부 확인
        if (adminCategoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }

        // 3. Category Entity 생성
        Category category = Category.builder()
                .name(request.getName())
                .build();

        // 4. DB 저장
        Category savedCategory = adminCategoryRepository.save(category);

        // 5. Response DTO 생성 및 반환
        return AdminCategoryDto.Response.builder()
                .categoryId(savedCategory.getCategoryId())
                .name(savedCategory.getName())
                .build();
    }

    /**
     * 게시판 카테고리를 수정
     *
     * @param categoryId 수정할 카테고리 ID
     * @param request 수정할 카테고리 정보
     * @return 수정된 카테고리 정보
     */
    public AdminCategoryDto.Response updateCategory(
            Long categoryId,
            AdminCategoryDto.UpdateRequest request) {

        // 1. 카테고리 관리는 관리자만 가능하기에 권한이 Admin인지 확인
        adminAuthorizationService.validateAdmin();

        // 2. categoryId로 카테고리 조회
        Category category = adminCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        // 3. 존재 여부 확인
        // findById()에서 확인 완료

        // 4. 이름 중복 여부 확인
        if (adminCategoryRepository.existsByName(request.getName())
                && !category.getName().equals(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }

        // 5. 카테고리 정보 수정
        category.updateName(request.getName());

        // 6. Response DTO 생성 및 반환
        return AdminCategoryDto.Response.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .build();
    }

    /**
     * 게시판 카테고리 목록을 조회
     *
     * @return 전체 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<AdminCategoryDto.Response> getAllCategories() {

        // 1. 카테고리 관리는 관리자만 가능하기에 권한이 Admin인지 확인
        adminAuthorizationService.validateAdmin();

        // 2. 전체 카테고리 조회
        List<Category> categories = adminCategoryRepository.findAll();

        // 3. Response DTO 리스트로 변환
        List<AdminCategoryDto.Response> responses = categories.stream()
                .map(category -> AdminCategoryDto.Response.builder()
                        .categoryId(category.getCategoryId())
                        .name(category.getName())
                        .build())
                .toList();

        // 4. 반환
        return responses;
    }

    /**
     * 게시판 카테고리를 삭제
     *
     * @param categoryId 삭제할 카테고리 ID
     */
    public void deleteCategory(Long categoryId) {

        // 1. 카테고리 관리는 관리자만 가능하기에 권한이 Admin인지 확인
        adminAuthorizationService.validateAdmin();

        // 2. categoryId로 카테고리 조회
        Category category = adminCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        // 3. 존재 여부 확인
        // findById()에서 확인 완료

        // 4. 삭제
        adminCategoryRepository.delete(category);
    }
}
