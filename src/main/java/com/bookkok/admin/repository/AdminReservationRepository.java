package com.bookkok.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookkok.reservation.entity.Reservation;

public interface AdminReservationRepository extends JpaRepository<Reservation, Long> {

}