package com.bookkok.board.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 20)
    private String name = "잡담";

    @Builder
    private Category(String name){
        this.name = (name == null || name.isBlank()) ? "잡담" : name;
    }

    public void updateName(String name) {
        this.name = (name == null || name.isBlank()) ? "잡담" : name;
    }
}
