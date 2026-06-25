package com.bookkok.board.controller;

import com.bookkok.board.dto.PostDto;
import com.bookkok.board.service.PostService;
import com.bookkok.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> createPost(
            @RequestBody PostDto.CreateRequest request,
            @AuthenticationPrincipal User loginUser
            ){
        Long postId = postService.createPost(request, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    @GetMapping
    public ResponseEntity<List<PostDto.ListResponse>> getAllPosts(){
        List<PostDto.ListResponse> response = postService.getAllPosts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.DetailResponse> getPostDetail(@PathVariable Long postId){
        PostDto.DetailResponse response = postService.getPostDetail(postId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto.DetailResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostDto.UpdateRequest request,
            @AuthenticationPrincipal User loginUser){

        PostDto.DetailResponse response = postService.updatePost(postId, request, loginUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal User loginUser){
        postService.deletePost(postId, loginUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal User loginUser){

        postService.toggleLike(postId, loginUser);
        return ResponseEntity.ok().build();
    }
}
