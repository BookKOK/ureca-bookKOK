package com.bookkok.club.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "club")
@Getter
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long clubId;

    @Column(name = "leader_member_id")
    private Long leaderMemberId;

    @Column(name = "club_name")
    private String clubName;

    @Column(name = "description")
    private String description;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(nullable = false)
    private int headcount;

    protected Club() {} //constructor

}
