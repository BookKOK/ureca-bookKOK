package com.bookkok.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.bookkok.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private LocalDate reservationDate;
        private String reservationCourt;
        private List<LocalTime> reservationTimes;

        @Builder
        private CreateRequest(LocalDate reservationDate, String reservationCourt, List<LocalTime> reservationTimes) {
            this.reservationDate = reservationDate;
            this.reservationCourt = reservationCourt;
            this.reservationTimes = reservationTimes;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ListResponse{
        private Long reservationId;
        private String clubName;
        private LocalDate reservationDate;
        private String reservationCourt;
        private LocalTime reservationTime;

        @Builder
        private ListResponse(Long reservationId, String clubName, LocalDate reservationDate
                , String reservationCourt, LocalTime reservationTime){
            this.reservationId = reservationId;
            this.clubName = clubName;
            this.reservationDate = reservationDate;
            this.reservationCourt = reservationCourt;
            this.reservationTime = reservationTime;
        }

        public static ListResponse from(Reservation reservation){
            return ListResponse.builder()
                    .reservationId(reservation.getReservationId())
                    .clubName(reservation.getClub().getClubName())
                    .reservationDate(reservation.getReservationDate())
                    .reservationCourt(reservation.getReservationCourt())
                    .reservationTime(reservation.getReservationTime())
                    .build();
        }
    }

}
