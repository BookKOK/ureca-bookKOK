package com.bookkok.board.repository;

import com.bookkok.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시물 댓글 모아보기
    List<Comment> findByPost_PostId(Long postId);

    // 댓글 작성일순 정렬
    List<Comment> findByPost_PostIdOrderByCreatedDateAsc(Long postId);
}
