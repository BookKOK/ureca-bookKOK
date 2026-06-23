package com.bookkok.club.controller;

import com.bookkok.club.dto.ClubDto;
import com.bookkok.club.entity.Club;
import com.bookkok.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    //1. 단체 개설
    @PostMapping
    public ResponseEntity<Long> createClub(@RequestParam String leaderId,
                                           @RequestBody ClubDto.CreateRequest request) {

        Long createdClubId = clubService.createClub(leaderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClubId);
    }

    //2. 단체 상세 조회 (GET /api/clubs/{clubId})
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDto.DetailResponse> getClub(@PathVariable Long clubId) {
        Club foundClub = clubService.findClubById(clubId);

        return ResponseEntity.ok(ClubDto.DetailResponse.from(foundClub));
    }

    //3. 단체 전체 목록 조회 (GET /api/clubs)
    @GetMapping
    public ResponseEntity<List<ClubDto.ListResponse>> getAllClubs() {
        List<ClubDto.ListResponse> responses = clubService.findAllClubs().stream()
                .map(ClubDto.ListResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    //4. 단체 이름 키워드 검색 (GET /api/clubs/search?keyword=테니스)
    @GetMapping("/search")
    public ResponseEntity<List<ClubDto.ListResponse>> searchClubs(@RequestParam String keyword) {
        List<ClubDto.ListResponse> responses = clubService.searchClubsByName(keyword).stream()
                .map(ClubDto.ListResponse::from)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    //5. 내 단체 조회
    @GetMapping("/my")
    public ResponseEntity<ClubDto.DetailResponse> getMyClub(@RequestParam String leaderId) {
        Club myClub = clubService.findClubByLeader(leaderId);
        return ResponseEntity.ok(ClubDto.DetailResponse.from(myClub));
    }

}
