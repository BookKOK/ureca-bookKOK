package com.bookkok.admin.controller;

import com.bookkok.admin.dto.AdminReservationDto;
import com.bookkok.admin.service.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    /**
     * 전체 예약 조회
     *
     * 관리자 전체 예약 정보 조회
     *
     * @return 전체 예약 목록
     */
    @GetMapping
    public ResponseEntity<List<AdminReservationDto.SummaryResponse>> getAllReservations() {

        return ResponseEntity.ok(
                adminReservationService.getAllReservations());
    }

    /**
     * 예약 검색
     *
     * 검색어를 이용한 예약 정보 검색
     *
     * @param keyword 검색어
     * @return 검색된 예약 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<AdminReservationDto.SummaryResponse>> searchReservations(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                adminReservationService.searchReservations(keyword));
    }

    /**
     * 예약 강제 취소
     *
     * 관리자 예약 강제 취소
     *
     * @param reservationId 예약 ID
     * @return 예약 강제 취소 결과
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<AdminReservationDto.ForceCancelResponse> cancelReservation(
            @PathVariable Long reservationId) {

        return ResponseEntity.ok(
                adminReservationService.cancelReservation(reservationId));
    }

}