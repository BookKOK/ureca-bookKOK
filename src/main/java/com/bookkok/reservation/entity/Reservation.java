package com.bookkok.reservation.entity;

import com.bookkok.club.entity.Club;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "club_id", nullable = false)
    private Club club;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_court", nullable = false, length = 50)
    private String reservationCourt;

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    @Column(nullable = false)
    private int headcount;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDate createdDate;

    @Builder
    public Reservation(Club club, LocalDate reservationDate, String reservationCourt, LocalTime reservationTime, int headcount, LocalDate createdDate) {
        this.club = club;
        this.reservationDate = reservationDate;
        this.reservationCourt = reservationCourt;
        this.reservationTime = reservationTime;
        this.headcount = headcount;
        this.createdDate = createdDate;
    }

}
