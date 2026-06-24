package com.bookkok.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public class AdminReservationController {

    private final AdminReservationSrvice adminReservationServicel;


    /**
     * 전체 예약 조회
     *
     * 관리자 전체 예약 정보 조회
     *
     * @return 전체 예약 목록
     */
    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        return ResponseEntity.ok(adminReservationService.getAllReservations());
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
    public ResponseEntity<?> searchReservations(@RequestParam String keyword) {
        return ResponseEntity.ok(adminReservationService.searchReservations(keyword));
    }

    /**
     * 예약 강제 취소
     *
     * 관리자 예약 강제 취소
     *
     * @param reservationId 예약 ID
     * @return 취소 완료 메시지
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        adminReservationService.cancelReservation(reservationId);
        return ResponseEntity.ok("예약이 강제 취소되었습니다.");
    }

}
