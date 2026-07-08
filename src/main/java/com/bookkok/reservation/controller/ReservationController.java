package com.bookkok.reservation.controller;

import com.bookkok.reservation.dto.ReservationDto;
import com.bookkok.reservation.entity.Reservation;
import com.bookkok.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 1. 예약 생성 [HTTP POST /api/reservations]
     * @param request : 클라이언트로부터 수신한 json 데이터 (예약 날짜, 시간, 코트, 인원, 소속 단체 id)
     * @param principal : 현재 로그인한 사용자의 인증 정보
     * @return http 201 (Created) 헤더 및 생성된 예약의 식별자 번호
     * 중복 예약이 존재하거나 예약하는 단체 id가 실재하지 않을 경우 예외 발생
     */
    @PostMapping
    public ResponseEntity<Long> createReservation(@RequestBody ReservationDto.CreateRequest request,
                                                  Principal principal) {
        Long reservationId = reservationService.createReservation(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationId);
    }

    /**
     * 2. 달력에서 예약 조회 [HTTP GET /api/reservations/calendar?startDate={startDate}&endDate={endDate}]
     * @param startDate : 조회 시작 일자 (yyyy-MM-dd 규격)
     * @param endDate : 조회 종료 일자 (yyyy-MM-dd 규격)
     * @return http 200 (OK) 및 예약 요약 정보 dto 리스트
     */
    @GetMapping("/calendar")
    public ResponseEntity<List<ReservationDto.ListResponse>> getCalendarReservations(
            @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {

        List<ReservationDto.ListResponse> responses = reservationService.findReservationsByCalendar(startDate, endDate)
                .stream()
                .map(ReservationDto.ListResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * 3. 특정 일자 코트의 예약 현황 조회 [HTTP GET /api/reservations/daily?date={date}]
     * @param date : 조회 대상 일자 (yyyy-MM-dd 규격)
     * @return http 200 (OK) 및 해당 일자에 마감된 예약 요약 정보 dto 리스트
     */
    @GetMapping("/daily")
    public ResponseEntity<List<ReservationDto.ListResponse>> getDailyReservations(@RequestParam LocalDate date) {
        List<ReservationDto.ListResponse> responses = reservationService.findReservationsByDate(date)
                .stream()
                .map(ReservationDto.ListResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * 4. 특정 단체의 예약 목록 조회 [HTTP GET /api/reservations/my?clubId={clubId}]
     * @param clubId : 조회 대상 단체의 식별자
     * @return http 200 (OK) 및 해당 단체가 소유한 예약의 요약 정보 dto 리스트
     */
    @GetMapping("/my")
    public ResponseEntity<List<ReservationDto.ListResponse>> getMyReservations(@RequestParam Long clubId) {
        List<ReservationDto.ListResponse> responses = reservationService.findReservationsByClub(clubId)
                .stream()
                .map(ReservationDto.ListResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * 5. 예약 단건 상세 조회 [HTTP GET /api/reservations/{reservationId}]
     * @param reservationId : 경로 변수로 유입된 대상 예약의 식별자
     * @return http 200 (OK) 및 예약 상세 데이터가 직렬화된 dto 인스턴스
     * 전달받은 식별자에 대응하는 데이터가 db에 없을 경우 예외 발생
     */
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDto.DetailResponse> getReservationDetail(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.findReservationById(reservationId);
        return ResponseEntity.ok(ReservationDto.DetailResponse.from(reservation));
    }

    /**
     * 6. 예약 취소 [HTTP DELETE /api/reservations/{reservationId}]
     * @param reservationId : 경로 변수로 유입된 대상 예약의 식별자
     * @return http 204 (No Content)
     * 전달받은 식별자에 대응하는 데이터가 db에 없을 경우 예외 발생
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId) {
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 7. 예약 정보 입력 화면 진입 시 로그인 유저의 정보 조회 [GET /api/reservations/info]
     * @param principal : Spring Security를 통해 인증된 현재 로그인 사용자 정보
     * @return HTTP 200 (OK) 및 예약자 정보 응답 DTO
     */
    @GetMapping("/info")
    public ResponseEntity<ReservationDto.ReserverResponse> getReserverInfo(Principal principal) {
        ReservationDto.ReserverResponse response = reservationService.getReserverInfo(principal.getName());
        return ResponseEntity.ok(response);
    }
}
