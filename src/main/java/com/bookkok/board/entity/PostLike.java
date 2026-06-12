package com.bookkok.board.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long postLikeId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    protected PostLike() {}
}
