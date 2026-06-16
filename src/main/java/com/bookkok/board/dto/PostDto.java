package com.bookkok.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostDto {

    // 글 쓸 때
    @Getter
    @NoArgsConstructor
    public static class CreateRequest{
        private Long categoryId;
        private String title;
        private String content;

        @Builder
        private CreateRequest(Long categoryId, String title, String content){
            this.categoryId = categoryId;
            this.title = title;
            this.content = content;
        }
    }

    // 글 목록 조회
    @Getter
    @NoArgsConstructor
    public static class ListResponse{
        private Long postId;
        private String authorName;
        private String categoryName;
        private String title;
        private int viewCount;
        private int likeCount;

        @Builder
        private ListResponse(Long postId, String authorName, String categoryName,
                             String title, int viewCount, int likeCount){
            this.postId = postId;
            this.authorName = authorName;
            this.categoryName = categoryName;
            this.title = title;
            this.viewCount = viewCount;
            this.likeCount = likeCount;
        }
    }

    // 글 상세 조회
    @Getter
    @NoArgsConstructor
    public static class DetailResponse{
        private Long postId;
        private String authorName;      // ID 대신 이름만
        private String categoryName;    // ID 대신 이름만
        private String title;
        private String content;
        private int viewCount;
        private int likeCount;
        private LocalDateTime createdDate;

        @Builder
        private DetailResponse(Long postId, String authorName, String categoryName, String title,
                               String content, int viewCount, int likeCount, LocalDateTime createdDate){
            this.postId = postId;
            this.authorName = authorName;
            this.categoryName = categoryName;
            this.title = title;
            this.content = content;
            this.viewCount = viewCount;
            this.likeCount = likeCount;
            this.createdDate = createdDate;
        }
    }
}
