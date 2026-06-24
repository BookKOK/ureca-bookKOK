package com.bookkok.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookkok.reservation.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface AdminReservationRepository extends JpaRepository<Reservation, Long> {

    // 예약 날짜 검색
    List<Reservation> findByReservationDate(LocalDate reservationDate);

}