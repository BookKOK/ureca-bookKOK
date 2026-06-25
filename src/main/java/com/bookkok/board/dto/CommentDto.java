package com.bookkok.board.dto;

import com.bookkok.board.entity.Comment;
import com.bookkok.board.entity.Post;
import com.bookkok.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommentDto {

    // 댓글 작성
    @Getter
    @NoArgsConstructor
    public static class CreateRequest{
        private Long postId;
        private String content;

        @Builder
        private CreateRequest(Long postId, String content){
            this.postId = postId;
            this.content = content;
        }

        public Comment toEntity(Post post, User loginUser){
            return Comment.builder()
                    .content(this.content)
                    .post(post)
                    .authorMember(loginUser)
                    .build();
        }
    }

    // 댓글 수정
    @Getter
    @NoArgsConstructor
    public static class UpdateRequest{
        private String content;

        @Builder
        private UpdateRequest(String content){
            this.content = content;
        }
    }

    // 댓글 조회
    @Getter
    @NoArgsConstructor
    public static class Response{
        private Long commentId;
        private String authorName;
        private String content;
        private LocalDateTime createdDate;
        private boolean isModified;     // 날짜 대신 수정 여부 전달 - true면 (수정됨)

        @Builder
        private Response(Long commentId, String authorName, String content,
                                LocalDateTime createdDate, boolean isModified){
            this.commentId = commentId;
            this.authorName = authorName;
            this.content = content;
            this.createdDate = createdDate;
            this.isModified = isModified;
        }

        public static Response from(Comment comment){
            boolean modified = comment.getModifiedDate() != null
                    && !comment.getCreatedDate().isEqual(comment.getModifiedDate());
            return Response.builder()
                    .commentId(comment.getCommentId())
                    .authorName(comment.getAuthorMember().getMemberId())
                    .content(comment.getContent())
                    .createdDate(comment.getCreatedDate())
                    .isModified(modified)
                    .build();
        }

    }
}