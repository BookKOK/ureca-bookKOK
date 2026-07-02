package com.bookkok.reservation.repository;

import com.bookkok.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    //달력에서 예약 현황 조회
    List<Reservation> findByReservationDateBetween(LocalDate startDate, LocalDate endDate);

    //날짜별 예약 현황 조회
    List<Reservation> findByReservationDate(LocalDate reservationDate);

    //우리 단체(나)의 예약 현황을 목록으로 조회
    List<Reservation> findByClub_ClubId(Long clubId);

    //단체Id, 날짜, 코트 기반 삭제
    List<Reservation> deleteByClubIdDateCourt(
            Long clubId, LocalDate reservationDate, String reservationCourt
    );

    //특정 날짜/시간/코트에 이미 예약이 있는지 중복 체크 (동시성 방지)
    boolean existsByReservationDateAndReservationTimeAndReservationCourt(
            LocalDate date, LocalTime time, String court
    );

}
