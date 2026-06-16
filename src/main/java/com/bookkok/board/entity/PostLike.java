package com.bookkok.board.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Member;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@Getter
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long postLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User user;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    protected PostLike() {}

    @Builder
    private PostLike(Post post, User user){
        this.post = post;
        this.user = user;
    }
}
