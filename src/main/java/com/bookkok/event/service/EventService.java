package com.bookkok.event.service;


import com.bookkok.club.entity.Club;
import com.bookkok.club.repository.ClubRepository;
import com.bookkok.event.dto.EventDto;
import com.bookkok.member.entity.Member;
import com.bookkok.member.entity.RoleType;
import com.bookkok.reservation.entity.Reservation;
import com.bookkok.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final ReservationRepository reservationRepository;
    private final ClubRepository clubRepository;

    @Transactional
    public void createEventReservations(EventDto.CreateRequest request, Member loginMember){

        if(loginMember == null || loginMember.getRoleName() != RoleType.ADMIN){
            throw new IllegalArgumentException("행사 등록은 관리자만 가능합니다.");
        }

        Club adminClub = clubRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("시스템 관리자 클럽이 존재하지 않습니다."));

        for(String court : request.getReservationCourts()) {
            for (LocalTime time : request.getReservationTimes()) {
                Reservation reservation = Reservation.builder()
                        .club(adminClub)
                        .reservationDate(request.getReservationDate())
                        .reservationCourt(court)
                        .reservationTime(time)
                        .headcount(0)
                        .build();
                reservationRepository.save(reservation);
            }
        }
    }

    public List<EventDto.ListResponse> getAllEvents() {
        List<Reservation> adminReservations = reservationRepository.findByClub_ClubId(1L);

        return adminReservations.stream()
                .map(EventDto.ListResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEvent(LocalDate reservationDate, String reservationCourt){
        reservationRepository.deleteByClubIdDateCourt(1L, reservationDate, reservationCourt);
    }

    @Transactional
    public void updateEvent(LocalDate originalDate, String originalCourt, EventDto.UpdateRequest request, Member loginMember){

        if(loginMember == null || loginMember.getRoleName() != RoleType.ADMIN){
            throw new IllegalArgumentException("행사 수정은 관리자만 가능합니다.");
        }

        reservationRepository.deleteByClubIdDateCourt(1L, originalDate, originalCourt);

        Club adminClub = clubRepository.findById(1L).orElseThrow();

        for(LocalTime newTime : request.getNewReservationTimes()){
            Reservation reservation = Reservation.builder()
                    .club(adminClub)
                    .reservationDate(request.getReservationDate())
                    .reservationCourt(request.getReservationCourt())
                    .reservationTime(newTime)
                    .headcount(0)
                    .build();
            reservationRepository.save(reservation);
        }
    }
}
