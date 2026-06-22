package com.bookkok.board.repository;

import com.bookkok.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 카테고리로에 속한 게시글
    List<Post> findByCategory_CategoryId(Long categoryId);

    // 좋아요순 정렬
    List<Post> findAllByOrderByLikeCountDesc();

    // 최신순 정렬
    List<Post> findAllByOrderByCreatedDateDesc();

    // 제목 / 내용으로 검색
    List<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);
}


