package com.bookkok.club.service;

import com.bookkok.club.dto.ClubDto;
import com.bookkok.club.entity.Club;
import com.bookkok.club.repository.ClubRepository;
import com.bookkok.member.entity.Member;
import com.bookkok.member.entity.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //읽기 전용으로 설정하여 성능 최적화
@RequiredArgsConstructor
@Log4j2
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

        Member leader = clubRepository.findMemberByMemberId(leaderId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        leader.updateClubId(savedClub);
        if (leader.getRoleName() != RoleType.ADMIN) leader.updateRoleName(RoleType.MEMBER);

        log.info("createClub 진입");

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
        log.info("findAllClubs 진입");
        return clubRepository.findAll();
    }

    /**
     * 4. 단체 회원 목록 조회
     * @param clubId : 조회를 원하는 단체의 고유 아이디
     * @return 회원 목록
     */
    public List<ClubDto.MemberResponse> getClubMembers(Long clubId) {
        return clubRepository.findMembersByClubId(clubId).stream()
                .map(ClubDto.MemberResponse::from)
                .toList();
    }

    /**
     * 5. 단체 탈퇴 (단체장은 불가)
     * @param clubId : 탈퇴를 원하는 단체의 고유 아이디
     * @param memberId : 탈퇴를 원하는 회원의 고유 아이디
     * @throws IllegalStateException : 전달받은 식별자(단체장 id)가 단체장이 아닌 경우
     * @throws IllegalArgumentException : 전달받은 식별자(회원 id)에 대응하는 회원이 db에 존재하지 않는 경우
     */
    @Transactional
    public void leaveClub(Long clubId, String memberId) {
        Club club = findClubById(clubId);
        if (club.getLeaderMemberId().equals(memberId)) {
            throw new IllegalStateException("단체장은 탈퇴할 수 없습니다.");
        }

        Member member = clubRepository.findMemberByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
        member.updateClubId(null);
        member.updateRoleName(RoleType.USER);
    }

    /**
     * 6. 단체 회원 강퇴 (단체장만 가능)
     * @param clubId : 강퇴를 원하는 단체의 고유 아이디
     * @param targetMemberId : 강퇴를 시키려는 회원의 고유 아이디
     * @param leaderId : 강퇴를 원하는 단체장의 고유 아이디
     * @throws IllegalStateException : 전달받은 식별자(단체장 id)가 단체장이 아닌 경우
     * @throws IllegalArgumentException : 전달받은 식별자(회원 id)에 대응하는 회원이 db에 존재하지 않는 경우
     */
    @Transactional
    public void kickMember(Long clubId, String targetMemberId, String leaderId) {
        Club club = findClubById(clubId);
        if (!club.getLeaderMemberId().equals(leaderId)) {
            throw new IllegalStateException("단체장만 강퇴할 수 있습니다.");
        }

        Member member = clubRepository.findMemberByMemberId(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("대상 회원을 찾을 수 없습니다."));
        member.updateClubId(null);
        member.updateRoleName(RoleType.USER);
    }

    /**
     * 7. 단체장 위임
     * @param clubId : 단체장을 위임하려는 단체의 고유 아이디
     * @param newLeaderId : 새로운 단체장 회원의 고유 아이디
     * @param curLeaderId : 기존 단체장 회원의 고유 아이디
     * @throws IllegalStateException : 전달받은 식별자(단체장 id)가 단체장이 아닌 경우
     */
    @Transactional
    public void handLeader(Long clubId, String newLeaderId, String curLeaderId) {
        log.info("newLeaderId: " + newLeaderId);

        Club club = findClubById(clubId);
        if (!club.getLeaderMemberId().equals(curLeaderId)) {
            throw new IllegalStateException("단체장만 위임할 수 있습니다.");
        }
        club.updateLeader(newLeaderId);
        clubRepository.save(club);

        Member curLead = clubRepository.findMemberByMemberId(curLeaderId)
                .orElseThrow(() -> new IllegalArgumentException("대상 회원을 찾을 수 없습니다."));
        Member newLead = clubRepository.findMemberByMemberId(newLeaderId)
                .orElseThrow(() -> new IllegalArgumentException("대상 회원을 찾을 수 없습니다."));
        curLead.updateRoleName(RoleType.MEMBER);
        newLead.updateRoleName(RoleType.LEADER);
    }

    /**
     * 8. 단체 삭제
     * @param clubId : 삭제를 원하는 단체의 고유 아이디
     * @param leaderId : 삭제를 원하는 단체의 단체장 고유 아이디
     * @throws IllegalStateException : 전달받은 식별자(단체장 id)가 단체장이 아닌 경우
     */
    @Transactional
    public void deleteClub(Long clubId, String leaderId) {
        Club club = findClubById(clubId);
        if (!club.getLeaderMemberId().equals(leaderId)) {
            throw new IllegalStateException("단체장만 삭제할 수 있습니다.");
        }

        List<Member> members = clubRepository.findMembersByClubId(clubId);
        for (Member member : members) {
            member.updateClubId(null);
            member.updateRoleName(RoleType.USER);
        }

        clubRepository.delete(club);
    }

    /**
     * 9. 단체 가입 요청 (자동 가입)
     * @param clubId : 가입할 단체의 고유 아이디
     * @param memberId : 가입할 회원의 고유 아이디
     */
    @Transactional
    public void joinClub(Long clubId, String memberId) {
        System.out.println("DEBUG: 조회 시도하는 memberId = [" + memberId + "]");
        log.info("가입 시도: clubId={}, memberId={}", clubId, memberId);

        Club club = findClubById(clubId);
        Member member = clubRepository.findMemberByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        if (member.getClub() != null) {
            throw new IllegalStateException("이미 단체에 가입되어 있습니다.");
        }

        member.updateClubId(club);
        if (member.getRoleName() != RoleType.ADMIN) member.updateRoleName(RoleType.MEMBER);
    }

    /**
     * 10. 단체 이름 키워드로 검색
     * @param keyword : 검색할 키워드 문자열
     * @return 키워드가 포함된 단체 엔티티 리스트 (검색 결과가 없을 경우 null이 아닌 텅 빈 List 반환)
     */
    public List<Club> searchClubsByName(String keyword) {
        return clubRepository.findByClubNameContaining(keyword);
    }

//    /**
//     * 11. 단체장 이름으로 검색
//     * @param leaderMemberId : 단체장의 고유 계정 아이디 (memberId)
//     * @return 해당 회원이 개설한 단체 엔티티 인스턴스
//     * @throws IllegalArgumentException : 전달받은 계정 아이디로 개설된 단체가 db에 존재하지 않는 경우
//     */
//    public Club findClubByLeader(String leaderMemberId) {
//        return clubRepository.findByLeaderMemberId(leaderMemberId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 개설한 단체가 없습니다."));
//    }

}
