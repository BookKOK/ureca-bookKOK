package com.bookkok.admin.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.bookkok.reservation.entity.Reservation;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자 예약 관리 API에서 사용하는 요청/응답 DTO 묶음입니다.
public class AdminReservationDto {

    // 전체 예약 조회와 예약 검색에서 사용할 필터 조건입니다.
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchCondition {
        private Long clubId;
        private String clubName;
        private String memberId;
        private String reservationCourt;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    // 예약 목록 화면에 노출할 예약 요약 정보입니다.
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryResponse {
        private Long reservationId;
        private Long clubId;
        private String clubName;
        private LocalDate reservationDate;
        private String reservationCourt;
        private LocalTime reservationTime;
        private int headcount;
        private LocalDate createdDate;

        public static SummaryResponse from(Reservation reservation) {
            return SummaryResponse.builder()
                    .reservationId(reservation.getReservationId())
                    .clubId(reservation.getClub().getClubId())
                    .clubName(reservation.getClub().getClubName())
                    .reservationDate(reservation.getReservationDate())
                    .reservationCourt(reservation.getReservationCourt())
                    .reservationTime(reservation.getReservationTime())
                    .headcount(reservation.getHeadcount())
                    .createdDate(reservation.getCreatedDate())
                    .build();
        }
    }

    // 예약 상세 조회에서 사용할 예약 상세 정보입니다.
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailResponse {
        private Long reservationId;
        private Long clubId;
        private String clubName;
        private LocalDate reservationDate;
        private String reservationCourt;
        private LocalTime reservationTime;
        private int headcount;
        private LocalDate createdDate;
    }

    // 관리자가 예약을 강제로 취소할 때 입력하는 사유입니다.
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ForceCancelRequest {
        @NotBlank
        private String reason;
    }

    // 예약 강제 취소 처리 결과입니다.
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ForceCancelResponse {
        private Long reservationId;
        private String reason;
        private LocalDate canceledAt;
    }

    private AdminReservationDto() {
    }
}
