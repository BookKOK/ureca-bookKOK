package com.bookkok.reservation.service;

import com.bookkok.club.entity.Club;
import com.bookkok.club.repository.ClubRepository;
import com.bookkok.reservation.dto.ReservationDto;
import com.bookkok.reservation.entity.Reservation;
import com.bookkok.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClubRepository clubRepository; //예약 주체인 Club 엔터티도 필요

    //1. 예약 생성
    @Transactional
    public Long createReservation(ReservationDto.CreateRequest request) {

        //날짜 + 시간 + 코트 기반의 중복 예약 존재 여부 확인 (동시성 방어)
        boolean isOverBooked = reservationRepository.existsByReservationDateAndReservationTimeAndReservationCourt(
                request.getReservationDate(),
                request.getReservationTime(),
                request.getReservationCourt()
        );
        if (isOverBooked) {
            throw new IllegalArgumentException("해당 날짜와 시간의 코트는 이미 예약이 마감되었습니다.");
        }

        //예약하는 단체가 실제로 존재하는지 검증
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단체입니다."));

        Reservation reservation = request.toEntity(club);
        Reservation savedReservation = reservationRepository.save(reservation);

        return savedReservation.getReservationId();
    }

    //2. 달력에서 예약 목록 조회
    public List<Reservation> findReservationsByCalendar(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findByReservationDateBetween(startDate, endDate);
    }

    //3. 특정 날짜의 예약 목록 조회
    public List<Reservation> findReservationsByDate(LocalDate date) {
        return reservationRepository.findByReservationDate(date);
    }

    //4. 특정 단체의 예약 목록 조회
    public List<Reservation> findReservationsByClub(Long clubId) {
        return reservationRepository.findByClub_ClubId(clubId);
    }

    //5. 예약 단건 상세 조회
    public Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
    }

}
