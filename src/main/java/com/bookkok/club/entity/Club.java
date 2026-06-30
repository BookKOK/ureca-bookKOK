package com.bookkok.club.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "clubs")
@Getter
@NoArgsConstructor
public class Club extends BaseTimeEntity {

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

    @Column(nullable = false)
    private int headcount;

    @Builder
    protected Club(String leaderMemberId, String clubName, String description, LocalDateTime createDate, int headcount) {
        this.leaderMemberId = leaderMemberId;
        this.clubName = clubName;
        this.description = description;
        this.headcount = headcount;
    }

    //단체장 위임 시 단체장 id 변경을 위한 메서드
    public void updateLeader(String newLeaderId) {
        this.leaderMemberId = newLeaderId;
    }

}
