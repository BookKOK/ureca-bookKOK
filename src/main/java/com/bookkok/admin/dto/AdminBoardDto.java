package com.bookkok.admin.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자 게시판 모니터링 API에서 사용하는 요청/응답 DTO 묶음입니다.
public class AdminBoardDto {

    // 관리자가 게시글을 확인할 때 필요한 게시글 정보입니다.
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostResponse {
        private Long postId;
        private String authorMemberId;
        private String authorName;
        private Long categoryId;
        private String categoryName;
        private String title;
        private String content;
        private int viewCount;
        private int likeCount;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
    }

    // 관리자가 댓글을 확인할 때 필요한 댓글 정보입니다.
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentResponse {
        private Long commentId;
        private Long postId;
        private String authorMemberId;
        private String authorName;
        private String content;
        private LocalDateTime createdDate;
    }

    // 게시글 또는 댓글 삭제 처리 시 관리자에게 입력받는 사유입니다.
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeleteRequest {
        @NotBlank
        private String reason;
    }

    private AdminBoardDto() {
    }
}
