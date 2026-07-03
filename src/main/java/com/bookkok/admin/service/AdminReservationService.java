package com.bookkok.admin.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookkok.admin.dto.AdminReservationDto;
import com.bookkok.admin.repository.AdminReservationRepository;
import com.bookkok.reservation.entity.Reservation;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 예약 관련 비즈니스 로직 처리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminReservationService {

    private final AdminReservationRepository adminReservationRepository;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 전체 예약 목록 조회
     *
     * @return 전체 예약 목록
     */
    @Transactional(readOnly = true)
    public List<AdminReservationDto.SummaryResponse> getAllReservations() {

        // 1. 관리자 권한 검증
        adminAuthorizationService.validateAdmin();

        // 2. 전체 예약 조회
        List<Reservation> reservations = adminReservationRepository.findAll();

        /*
        // 3. SummaryResponse 변환
        List<AdminReservationDto.SummaryResponse> responses =
                reservations.stream()
                        .map(AdminReservationDto.SummaryResponse::from)
                        .toList();

        // 4. 결과 반환
        return responses;
         */

        return reservations.stream()
                .map(AdminReservationDto.SummaryResponse::from)
                .toList();
    }

    /**
     * 예약 검색
     *
     * @param condition 검색 조건
     * @return 검색된 예약 목록
     */
    @Transactional(readOnly = true)
    public List<AdminReservationDto.SummaryResponse> searchReservations(
            AdminReservationDto.SearchCondition condition) {

        // 1. 관리자 권한 검증
        adminAuthorizationService.validateAdmin();

        // 2. 검색 조건 조회
        List<Reservation> reservations =
                adminReservationRepository.findByReservationDate(
                        condition.getStartDate());

        // 3. SummaryResponse 변환

        // 4. 결과 반환
        return reservations.stream()
                .map(AdminReservationDto.SummaryResponse::from)
                .toList();
    }

    /**
     * 예약 강제 취소
     *
     * @param reservationId 예약 ID
     * @return 예약 취소 결과
     */
    public AdminReservationDto.ForceCancelResponse cancelReservation(
            Long reservationId) {

        // 1. 관리자 권한 검증
        adminAuthorizationService.validateAdmin();

        // 2. 예약 조회
        Reservation reservation = adminReservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("예약이 존재하지 않습니다."));

        // 3. 예약 존재 여부 확인
        // findById()에서 확인 완료

        // 4. 예약 삭제
        // TODO : 예약 상태(Status) 변경 방식으로 수정 예정
        adminReservationRepository.delete(reservation);

        // 5. 삭제 결과 저장
        // TODO : 예약 상태(Status) 변경 시 save() 여부 확인

        // 6. ForceCancelResponse 생성
        AdminReservationDto.ForceCancelResponse response =
                AdminReservationDto.ForceCancelResponse.builder()
                        .reservationId(reservation.getReservationId())
                        .reason(null)
                        .canceledAt(LocalDate.now())
                        .build();

        // 7. 결과 반환
        return response;
    }
}