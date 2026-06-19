package com.bookkok.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookkok.board.entity.Comment;

public interface AdminCommentRepository extends JpaRepository<Comment, Long> {

}