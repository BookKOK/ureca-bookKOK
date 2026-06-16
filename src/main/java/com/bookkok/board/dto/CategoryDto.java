package com.bookkok.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryDto {
    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long categoryId;
        private String categoryName;

        @Builder
        private Response(Long categoryId, String categoryName){
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }
    }
}