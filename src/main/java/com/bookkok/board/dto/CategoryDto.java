package com.bookkok.board.dto;

import com.bookkok.board.entity.Category;
import com.bookkok.board.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest{
        private String categoryName;

        @Builder
        private CreateRequest(String categoryName){
            this.categoryName = categoryName;
        }

        public Category toEntity(){
            return Category.builder()
                    .name(this.categoryName)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Response{
        private Long categoryId;
        private String categoryName;

        @Builder
        private Response(Long categoryId, String categoryName){
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }

        public static Response from(Category category){
            return Response.builder()
                    .categoryId(category.getCategoryId())
                    .categoryName(category.getName())
                    .build();
        }

    }
}