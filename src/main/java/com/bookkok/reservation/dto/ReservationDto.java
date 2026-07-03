package com.bookkok.reservation.dto;

import com.bookkok.club.entity.Club;
import com.bookkok.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationDto {

    //예약자 정보 조회
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReserverResponse {
        private String reserverName;
        private String phoneNumber;
        private Long clubId;
        private String clubName;
    }

    //예약 생성 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private Long clubId;
        private LocalDate reservationDate;
        private String reservationCourt;
        private LocalTime reservationTime;
        private int headcount;

        public Reservation toEntity(Club club) {
            return Reservation.builder()
                    .club(club)
                    .reservationDate(this.reservationDate)
                    .reservationCourt(this.reservationCourt)
                    .reservationTime(this.reservationTime)
                    .headcount(this.headcount)
                    .build();
        }
    }

    //예약 목록/달력/내 예약 조회용 응답 DTO
    @Getter
    @Builder
    public static class ListResponse {
        private Long reservationId;
        private String clubName;
        private LocalDate reservationDate;
        private String reservationCourt;
        private LocalTime reservationTime;

        public static ListResponse from(Reservation reservation) {
            return ListResponse.builder()
                    .reservationId(reservation.getReservationId())
                    .clubName(reservation.getClub().getClubName())
                    .reservationDate(reservation.getReservationDate())
                    .reservationCourt(reservation.getReservationCourt())
                    .reservationTime(reservation.getReservationTime())
                    .build();
        }
    }

    //예약 상세 조회용 응답 DTO
    @Getter
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

        public static DetailResponse from(Reservation reservation) {
            return DetailResponse.builder()
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

}
