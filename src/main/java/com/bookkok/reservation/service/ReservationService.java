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

    /**
     * 1. 예약 생성
     * @param request : 예약 날짜, 시간, 코트, 인원수, 소속 단체 id가 포함된 생성 요청 dto
     * @return : db에 정상 저장된 예약 데이터의 식별값
     * @throws IllegalArgumentException : 1. 요청된 날짜/시간/코트가 이미 예약 마감된 경우
     *                                    2. 전달받은 단체 id가 db에 존재하지 않을 경우
     */
    @Transactional
    public Long createReservation(ReservationDto.CreateRequest request) {

        //날짜 + 시간 + 코트 기반의 중복 예약 존재 여부 확인 (동시성)
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

    /**
     * 2. 달력에서 예약 조회
     * @param startDate : 조회 시작 일자
     * @param endDate : 조회 종료 일자
     * @return 조회된 예약 엔티티 목록 (데이터가 없을 경우 null이 아닌 텅 빈 List 반환)
     */
    public List<Reservation> findReservationsByCalendar(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findByReservationDateBetween(startDate, endDate);
    }

    /**
     * 3. 특정 날짜의 예약 목록 조회
     * @param date : 조회 대상 일자
     * @return 해당 일자의 예약 엔티티 목록
     */
    public List<Reservation> findReservationsByDate(LocalDate date) {
        return reservationRepository.findByReservationDate(date);
    }

    /**4. 특정 단체의 예약 목록 조회
     *
     * @param clubId : 조회 대상 단체의 식별자
     * @return 해당 단체의 예약 엔티티 목록
     */
    public List<Reservation> findReservationsByClub(Long clubId) {
        return reservationRepository.findByClub_ClubId(clubId);
    }

    /**
     * 5. 예약 단건 상세 조회
     * @param reservationId : 조회 대상 예약의 식별자
     * @return 조회된 예약 엔티티 인스턴스
     * @throws IllegalArgumentException : 전달받은 id에 대응하는 예약 데이터가 없을 경우
     */
    public Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
    }

    /**
     * 6. 예약 취소
     * @param reservationId : 취소하려는 예약의 식별자
     * @throws IllegalArgumentException : 전달받은 식별자에 대응하는 예약 데이터가 없는 경우
     */
    @Transactional
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        //추후 단체장만 예약 삭제 기능 필요시 추가

        reservationRepository.delete(reservation);
    }

}
