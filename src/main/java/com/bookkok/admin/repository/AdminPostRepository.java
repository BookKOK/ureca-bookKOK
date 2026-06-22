package com.bookkok.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookkok.board.entity.Post;

public interface AdminPostRepository extends JpaRepository<Post, Long> {

}