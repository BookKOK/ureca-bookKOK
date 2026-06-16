package com.bookkok.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자 카테고리 관리 API에서 사용하는 요청/응답 DTO 묶음입니다.
public class AdminCategoryDto {

    // 게시판 카테고리 추가 요청 값입니다.
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank
        @Size(max = 20)
        private String name;
    }

    // 게시판 카테고리 수정 요청 값입니다.
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @NotBlank
        @Size(max = 20)
        private String name;
    }

    // 카테고리 조회와 추가/수정 결과로 반환할 정보입니다.
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long categoryId;
        private String name;
    }

    private AdminCategoryDto() {
    }
}
