package com.bookkok.board.service;

import com.bookkok.board.dto.CategoryDto;
import com.bookkok.board.entity.Category;
import com.bookkok.board.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 등록
    @Transactional
    public Long createCategory(CategoryDto.CreateRequest request){
        if(categoryRepository.existsByName(request.getCategoryName())){
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }
        Category category = request.toEntity();
        Category savedCategory = categoryRepository.save(category);

        return savedCategory.getCategoryId();
    }

    //  카테고리 전체 조회
    public List<CategoryDto.Response> getAllCategory(){
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(CategoryDto.Response::from)
                .collect(Collectors.toList());
    }

}
