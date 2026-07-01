package com.bookkok.board.controller;

import com.bookkok.board.dto.PostDto;
import com.bookkok.board.service.PostService;
import com.bookkok.member.entity.Member;
import com.bookkok.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<Long> createPost(
            @RequestBody PostDto.CreateRequest request,
            @AuthenticationPrincipal User loginUser){

        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));
        Long postId = postService.createPost(request, loginMember);
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

        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));
        PostDto.DetailResponse response = postService.updatePost(postId, request, loginMember);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal User loginUser){
        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));
        postService.deletePost(postId, loginMember);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal User loginUser){

        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));
        postService.toggleLike(postId, loginMember);
        return ResponseEntity.ok().build();
    }
}
