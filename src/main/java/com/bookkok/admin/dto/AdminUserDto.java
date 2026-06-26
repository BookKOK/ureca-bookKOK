package com.bookkok.admin.dto;

import java.time.LocalDateTime;

import com.bookkok.user.entity.Provider;
import com.bookkok.user.entity.RoleType;

import com.bookkok.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자 회원 관리 API에서 사용하는 요청/응답 DTO
public class AdminUserDto {

    // 회원 목록 조회와 회원 검색에서 사용할 필터 조건입니다.
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchCondition {
        private String keyword;
        private RoleType roleName;
        private Long clubId;
        private Boolean blocked;
    }

    // 회원 목록 화면에 노출할 최소 회원 정보
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryResponse {

        private String memberId;
        private String name;
        private String email;
        private RoleType roleName;
        private String clubName;
        private Boolean blocked;

        public static SummaryResponse from(User user) {

            return SummaryResponse.builder()
                    .memberId(user.getMemberId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .roleName(user.getRoleName())
                    .clubName(user.getClub() != null
                            ? user.getClub().getClubName()
                            : null)
                    .blocked(user.getBlocked())
                    .build();
        }
    }

    // 회원 상세 조회에서 사용할 전체 회원 정보
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailResponse {

        private String memberId;
        private String name;
        private String email;
        private String phoneNumber;
        private RoleType roleName;
        private Long clubId;
        private String clubName;
        private Boolean blocked;
        private Provider provider;
        private LocalDateTime regDate;

        public static DetailResponse from(User user) {

            return DetailResponse.builder()
                    .memberId(user.getMemberId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .roleName(user.getRoleName())
                    .clubId(user.getClub() != null
                            ? user.getClub().getClubId()
                            : null)
                    .clubName(user.getClub() != null
                            ? user.getClub().getClubName()
                            : null)
                    .blocked(user.getBlocked())
                    .provider(user.getProvider())
                    .regDate(user.getRegDate())
                    .build();
        }
    }

    // 회원 차단 처리 시 관리자에게 입력받는 사유
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BlockRequest {
        @NotBlank
        private String reason;
    }

    // 회원 차단/차단 해제 처리 결과
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BlockResponse {
        private String memberId;
        private Boolean blocked;
        private LocalDateTime changedAt;

        public static BlockResponse from(User user) {

            return BlockResponse.builder()
                    .memberId(user.getMemberId())
                    .blocked(user.getBlocked())
                    .changedAt(LocalDateTime.now())
                    .build();
        }
    }

    private AdminUserDto() {
    }
}
