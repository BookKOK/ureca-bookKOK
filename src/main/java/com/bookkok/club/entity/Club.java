package com.bookkok.club.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clubs")
@Getter
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long clubId;

    @Column(name = "leader_member_id", length = 50)
    private String leaderMemberId;

    @Column(name = "club_name", nullable = false, length = 100)
    private String clubName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(nullable = false)
    private int headcount;

    public Club() {}

    @Builder
    protected Club(String leaderMemberId, String clubName, String description, LocalDateTime createDate, int headcount) {
        this.leaderMemberId = leaderMemberId;
        this.clubName = clubName;
        this.description = description;
        this.createDate = createDate;
        this.headcount = headcount;
    }

}
