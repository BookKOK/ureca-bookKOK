package com.bookkok.board.controller;

import com.bookkok.board.dto.CategoryDto;
import com.bookkok.board.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Long> createCategory(@RequestBody CategoryDto.CreateRequest request){

        Long categoryId = categoryService.createCategory(request);

        return ResponseEntity.ok(categoryId);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto.Response>> getAllCategory(){

        List<CategoryDto.Response> responses = categoryService.getAllCategory();

        return ResponseEntity.ok(responses);
    }

}
