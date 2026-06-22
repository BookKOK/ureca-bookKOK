package com.bookkok.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookkok.board.entity.Category;

public interface AdminCategoryRepository extends JpaRepository<Category, Long> {


//    카테고리명 중복 확인
    boolean existsByName(String name);


//    카테고리명 조회
    Optional<Category> findByName(String name);

}