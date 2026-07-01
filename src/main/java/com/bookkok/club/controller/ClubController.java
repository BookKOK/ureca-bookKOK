package com.bookkok.club.controller;

import com.bookkok.club.dto.ClubDto;
import com.bookkok.club.entity.Club;
import com.bookkok.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * 1. 단체 개설 [HTTP POST /api/clubs?leaderId={leaderId}]
     * @param leaderId : 단체 개설 요청을 보낸 단체장의 고유 계정 아이디 (memberId)
     * @param request : 생성할 단체의 이름과 소개글이 담긴 JSON 바디 데이터
     * @return HTTP 201 (Created) 및 생성된 단체의 고유 식별자 값
     */
    @PostMapping
    public ResponseEntity<Long> createClub(@RequestParam String leaderId,
                                           @RequestBody ClubDto.CreateRequest request) {

        Long createdClubId = clubService.createClub(leaderId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdClubId);
    }

    /**
     * 2. 단체 상세 조회 [HTTP GET /api/clubs/{clubId}]
     * @param clubId : URL 경로 변수로 전달된 대상 단체의 고유 식별자
     * @return HTTP 200 (OK) 및 단체 상세 정보가 직렬화된 DTO 응답 객체
     */
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDto.DetailResponse> getClub(@PathVariable Long clubId) {
        Club foundClub = clubService.findClubById(clubId);

        return ResponseEntity.ok(ClubDto.DetailResponse.from(foundClub));
    }

    /**
     * 3. 단체 전체 목록 조회 [HTTP GET /api/clubs]
     * @return HTTP 200 (OK) 및 단체 요약 정보 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<ClubDto.ListResponse>> getAllClubs() {
        List<ClubDto.ListResponse> responses = clubService.findAllClubs().stream()
                .map(ClubDto.ListResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * 4. 단체 회원 목록 조회 [HTTP GET /api/clubs/{clubId}/members]
     * @param clubId : URL 경로 변수로 전달된 대상 단체의 고유 식별자
     * @return HTTP 200 (OK) 및 해당 단체에 속한 회원 엔티티 리스트
     */
    @GetMapping("/{clubId}/members")
    public ResponseEntity<List<ClubDto.MemberResponse>> getClubMembers(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getClubMembers(clubId));
    }

    /**
     * 5. 단체 탈퇴 [HTTP DELETE /api/clubs/{clubId}/members/me]
     * @param clubId : 탈퇴를 원하는 단체의 고유 식별자
     * @param member : 현재 로그인한 사용자 객체
     * @return HTTP 204 (No Content)
     */
    @DeleteMapping("/{clubId}/members/me")
    public ResponseEntity<Void> leaveClub(@PathVariable Long clubId,
                                          @AuthenticationPrincipal User member) {
        String memberId = member.getUsername();
        clubService.leaveClub(clubId, memberId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 6. 단체 회원 강퇴 [HTTP DELETE /api/clubs/{clubId}/members/{targetMemberId}]
     * @param clubId : 강퇴를 원하는 단체의 고유 식별자
     * @param targetMemberId : 강퇴 대상 회원의 고유 계정 아이디
     * @param member : 요청을 보낸 단체장 객체
     * @return HTTP 204 (No Content)
     */
    @DeleteMapping("/{clubId}/members/{targetMemberId}")
    public ResponseEntity<Void> kickMember(@PathVariable Long clubId,
                                           @PathVariable String targetMemberId,
                                           @AuthenticationPrincipal User member) {
        String leaderId = member.getUsername();
        clubService.kickMember(clubId, targetMemberId, leaderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 7. 단체장 위임 [HTTP PATCH /api/clubs/{clubId}/leader]
     * @param clubId : 위임할 단체의 고유 식별자
     * @param request : 새로운 단체장의 계정 아이디가 담긴 JSON 바디 데이터
     * @param member : 기존 단체장 객체
     * @return HTTP 204 (No Content)
     */
    @PatchMapping("/{clubId}/leader")
    public ResponseEntity<Void> handLeader(@PathVariable Long clubId,
                                           @RequestBody ClubDto.LeaderChangeRequest request,
                                           @AuthenticationPrincipal User member) {
        String curLeaderId = member.getUsername();
        clubService.handLeader(clubId, request.getNewLeaderId(), curLeaderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 8. 단체 삭제 [HTTP DELETE /api/clubs/{clubId}]
     * @param clubId : 삭제를 원하는 단체의 고유 식별자
     * @param member : 삭제를 요청한 단체장 객체
     * @return HTTP 204 (No Content)
     */
    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long clubId,
                                           @AuthenticationPrincipal User member) {
        String leaderId = member.getUsername();
        clubService.deleteClub(clubId, leaderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 9. 단체 가입 [HTTP POST /api/clubs/{clubId}/join-requests]
     * @param clubId : 가입할 단체의 아이디
     * @param member : 현재 로그인한 사용자 객체
     * @return HTTP 200 (OK)
     */
    @PostMapping("/{clubId}/join-requests")
    public ResponseEntity<Void> joinClub(@PathVariable Long clubId,
                                         @AuthenticationPrincipal User member) {
        String memberId = member.getUsername();
        clubService.joinClub(clubId, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 10. 단체 이름 키워드 검색 [HTTP GET /api/clubs/search?keyword={keyword}]
     * @param keyword 검색할 단체명 키워드 문자열
     * @return HTTP 200 (OK) 및 검색 조건에 부합하는 단체 요약 정보 DTO 리스트
     */
    @GetMapping("/search")
    public ResponseEntity<List<ClubDto.ListResponse>> searchClubs(@RequestParam String keyword) {
        List<ClubDto.ListResponse> responses = clubService.searchClubsByName(keyword).stream()
                .map(ClubDto.ListResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

//    /**
//     * 11. 내 단체 조회 [HTTP GET /api/clubs/my?leaderId={leaderId}]
//     * @param leaderId 단체장의 고유 계정 아이디 (memberId)
//     * @return HTTP 200 (OK) 및 해당 단체의 상세 정보가 직렬화된 DTO 응답 객체
//     */
//    @GetMapping("/my")
//    public ResponseEntity<ClubDto.DetailResponse> getMyClub(@RequestParam String leaderId) {
//        Club myClub = clubService.findClubByLeader(leaderId);
//        return ResponseEntity.ok(ClubDto.DetailResponse.from(myClub));
//    }

    /**
     * 추가) 서비스 레이어에서 던진 예외를 프론트가 안전하게 읽을 수 있도록 400 에러로 변환
     * @param e 발생한 예외
     * @return 
     */
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    ResponseEntity<Map<String, String>> handleStateException(RuntimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
