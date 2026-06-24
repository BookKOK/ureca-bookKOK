package com.bookkok.club.service;

import com.bookkok.club.dto.ClubDto;
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

    /**
     * 1. 단체 개설 (생성)
     * @param leaderId : 개설 요청을 보낸 단체장의 고유 계정 ID (memberId)
     * @param request : 생성할 단체의 이름과 소개글이 담긴 DTO
     * @return 데이터베이스에 정상 저장된 단체의 고유 식별자 값
     * @throws IllegalArgumentException : 요청된 단체 이름이 이미 데이터베이스에 존재하는 경우
     */
    @Transactional
    public Long createClub(String leaderId, ClubDto.CreateRequest request) {
        if (clubRepository.existsByClubName(request.getClubName())) {
            throw new IllegalArgumentException("이미 존재하는 단체 이름입니다.");
        }

        Club club = request.toEntity(leaderId);

        Club savedClub = clubRepository.save(club);
        return savedClub.getClubId();
    }

    /**
     * 2. 단체 상세 조회
     * @param clubId : 조회 대상 단체의 고유 식별자
     * @return 영속성 컨텍스트에서 조회된 단체 엔티티 인스턴스
     * @throws IllegalArgumentException : 전달받은 식별자(단체 id)에 대응하는 단체가 db에 존재하지 않는 경우
     */
    public Club findClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단체입니다."));
    }

    /**
     * 3. 전체 단체 목록 조회
     * @return 전체 단체 리스트 (데이터가 없을 경우 null이 아닌 텅 빈 List 반환)
     */
    public List<Club> findAllClubs() {
        return clubRepository.findAll();
    }

    /**
     * 4. 단체 이름 키워드로 검색
     * @param keyword : 검색할 키워드 문자열
     * @return 키워드가 포함된 단체 엔티티 리스트 (검색 결과가 없을 경우 null이 아닌 텅 빈 List 반환)
     */
    public List<Club> searchClubsByName(String keyword) {
        return clubRepository.findByClubNameContaining(keyword);
    }

    /**
     * 5. 단체장 이름으로 검색
     * @param leaderMemberId : 단체장의 고유 계정 아이디 (memberId)
     * @return 해당 회원이 개설한 단체 엔티티 인스턴스
     * @throws IllegalArgumentException : 전달받은 계정 아이디로 개설된 단체가 db에 존재하지 않는 경우
     */
    public Club findClubByLeader(String leaderMemberId) {
        return clubRepository.findByLeaderMemberId(leaderMemberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 개설한 단체가 없습니다."));
    }

}
