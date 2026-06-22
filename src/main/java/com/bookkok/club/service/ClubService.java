package com.bookkok.club.service;

import com.bookkok.club.entity.Club;
import com.bookkok.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //읽기 전용으로 설정하여 성능 최적화
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;

    //1. 단체 개설 (생성)
    @Transactional
    public Long createClub(String leaderMemberId, String clubName, String description, int headcount) {
        if (clubRepository.existsByClubName(clubName)) {
            throw new IllegalArgumentException("이미 존재하는 단체 이름입니다.");
        }

        Club club = Club.builder()
                .leaderMemberId(leaderMemberId)
                .clubName(clubName)
                .description(description)
                .headcount(headcount)
                .build();

        Club savedClub = clubRepository.save(club);

        return savedClub.getClubId();
    }

    //2. 단체 상세 조회
    public Club findClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단체입니다."));
    }

    //3. 전체 단체 목록 조회
    public List<Club> findAllClubs() {
        return clubRepository.findAll();
    }

    //4. 단체 이름 키워드로 검색
    public List<Club> searchClubsByName(String keyword) {
        return clubRepository.findByClubNameContaining(keyword);
    }

    //5. 단체장 이름으로 검색
    public Club findClubByLeader(String leaderMemberId) {
        return clubRepository.findByLeaderMemberId(leaderMemberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 개설한 단체가 없습니다."));
    }

}
