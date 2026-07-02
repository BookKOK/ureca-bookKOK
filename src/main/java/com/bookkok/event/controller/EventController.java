package com.bookkok.event.controller;

import com.bookkok.event.dto.EventDto;
import com.bookkok.event.service.EventService;
import com.bookkok.member.entity.Member;
import com.bookkok.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<Void> createEvent(@RequestBody EventDto.CreateRequest requestDto,
                                            @AuthenticationPrincipal User loginUser){

        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));

        eventService.createEventReservations(requestDto, loginMember);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<EventDto.ListResponse>> getAllEvents(){
        List<EventDto.ListResponse> response = eventService.getAllEvents();
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Void> updateEvent(
            @RequestParam("reservationDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate originalDate,
            @RequestParam("reservationCourt") String originalCourt,
            @RequestBody EventDto.UpdateRequest request,
            @AuthenticationPrincipal User loginUser){
        Member loginMember = memberRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원찾을수없음"));

        eventService.updateEvent(originalDate, originalCourt, request, loginMember);

        return ResponseEntity.ok().build();
    }
}
