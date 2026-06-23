package com.bookkok.board.dto;

import com.bookkok.board.entity.Category;
import com.bookkok.board.entity.Post;
import com.bookkok.user.entity.User;
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

        public Post toEntity(User authorMember, Category category){
            return Post.builder()
                    .title(this.title)
                    .content(this.content)
                    .authorMember(authorMember)
                    .category(category)
                    .build();
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

        public static ListResponse from(Post post){
            return ListResponse.builder()
                    .postId(post.getPostId())
                    .authorName(post.getAuthorMember().getMemberId())
                    .categoryName(post.getCategory().getName())
                    .title(post.getTitle())
                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .build();
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

        public static DetailResponse from(Post post) {
            return DetailResponse.builder()
                    .postId(post.getPostId())
                    .authorName(post.getAuthorMember().getMemberId())
                    .categoryName(post.getCategory().getName())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .createdDate(post.getCreatedDate())
                    .build();
        }
    }
}
