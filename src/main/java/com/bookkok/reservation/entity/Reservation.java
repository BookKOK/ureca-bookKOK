package com.bookkok.reservation.entity;

import com.bookkok.club.entity.Club;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "club_id", nullable = false)
    private Club clubId;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_court", nullable = false, length = 50)
    private String reservationCourt;

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    @Column(nullable = false)
    private int headcount;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public Reservation() {}

    @Builder
    public Reservation(Club clubId, LocalDate reservationDate, String reservationCourt, LocalTime reservationTime, int headcount, LocalDateTime createdDate) {
        this.clubId = clubId;
        this.reservationDate = reservationDate;
        this.reservationCourt = reservationCourt;
        this.reservationTime = reservationTime;
        this.headcount = headcount;
        this.createdDate = createdDate;
    }

}
