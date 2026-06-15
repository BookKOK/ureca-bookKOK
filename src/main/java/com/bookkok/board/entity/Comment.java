package com.bookkok.board.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "author_member_id", nullable = false, length = 50)
    private String authorMemberId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    protected Comment() {}

    @Builder
    private Comment(Long postId, String authorMemberId, String content){
        this.postId = postId;
        this.authorMemberId = authorMemberId;
        this.content = content;
    }
}
