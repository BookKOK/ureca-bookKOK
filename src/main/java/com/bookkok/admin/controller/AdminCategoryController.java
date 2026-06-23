package com.bookkok.admin.controller;

import com.bookkok.admin.dto.AdminCategoryDto;
import com.bookkok.admin.service.AdminCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    /**
     * 게시판 카테고리를 생성
     *
     * @param request 생성할 카테고리 정보
     * @return 생성된 카테고리 정보
     */
    @PostMapping
    public ResponseEntity<AdminCategoryDto.Response> createCategory(
            @Valid @RequestBody AdminCategoryDto.CreateRequest request) {

        return ResponseEntity.ok(
                adminCategoryService.createCategory(request));
    }

    /**
     * 게시판 카테고리를 수정
     *
     * @param categoryId 수정할 카테고리 ID
     * @param request 수정할 카테고리 정보
     * @return 수정된 카테고리 정보
     */
    @PatchMapping("/{categoryId}")
    public ResponseEntity<AdminCategoryDto.Response> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody AdminCategoryDto.UpdateRequest request) {

        return ResponseEntity.ok(
                adminCategoryService.updateCategory(categoryId, request));
    }

    /**
     * 게시판 카테고리 목록을 조회
     *
     * @return 전체 카테고리 목록
     */
    @GetMapping
    public ResponseEntity<List<AdminCategoryDto.Response>> getAllCategories() {

        return ResponseEntity.ok(
                adminCategoryService.getAllCategories());
    }

    /**
     * 게시판 카테고리를 삭제
     *
     * @param categoryId 삭제할 카테고리 ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId) {

        adminCategoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}