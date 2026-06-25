package com.bookkok.club.dto;

import com.bookkok.club.entity.Club;
import com.bookkok.member.entity.RoleType;
import com.bookkok.member.entity.Member;

import lombok.*;

import java.time.LocalDateTime;

public class ClubDto {

    //단체 생성 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private String clubName;
        private String description;

        public Club toEntity(String leaderId) {
            return Club.builder()
                    .leaderMemberId(leaderId)
                    .clubName(this.clubName)
                    .description(this.description)
                    .headcount(1)
                    .build();
        }
    }

    //단체장 변경 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class LeaderChangeRequest {
        private String newLeaderId;
    }

    //단체 목록 조회용 응답 DTO
    @Getter
    @Builder
    public static class ListResponse {
        private Long clubId;
        private String clubName;
        private int headcount;

        public static ListResponse from(Club club) {
            return ListResponse.builder()
                    .clubId(club.getClubId())
                    .clubName(club.getClubName())
                    .headcount(club.getHeadcount())
                    .build();
        }
    }

    //단체 상세 조회용 응답 DTO
    @Getter
    @Builder
    public static class DetailResponse {
        private Long clubId;
        private String leaderMemberId;
        private String clubName;
        private String description;
        private LocalDateTime createDate;
        private int headcount;

        public static DetailResponse from(Club club) {
            return DetailResponse.builder()
                    .clubId(club.getClubId())
                    .leaderMemberId(club.getLeaderMemberId())
                    .clubName(club.getClubName())
                    .description(club.getDescription())
                    .createDate(club.getCreateDate())
                    .headcount(club.getHeadcount())
                    .build();
        }
    }

    //단체 회원 조회용 응답 DTO
    @Getter
    @Builder
    public static class MemberResponse {
        private String memberId;
        private String name;
        private RoleType roleName;

        public static MemberResponse from(Member user) {
            return MemberResponse.builder()
                    .memberId(user.getMemberId())
                    .name(user.getName())
                    .roleName(user.getRoleName())
                    .build();
        }
    }

}
