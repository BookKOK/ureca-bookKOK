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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "author_member_id", nullable = false)
    private User authorMember;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    protected Comment() {}

    @Builder
    private Comment(Post post, User authorMember, String content){
        this.post = post;
        this.authorMember = authorMember;
        this.content = content;
    }
}
