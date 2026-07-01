//단체 목록 기본
async function renderClubs() {
    setApp(html`
        <section class="wide-panel">
            <div class="search-box">
                <div class="search-line">
                    <input class="field" id="clubKeyword" placeholder="단체명">
                    <button class="primary" id="clubSearch" type="button">검색</button>
                </div>
            </div>
            <table class="table">
                <thead><tr><th>단체명</th><th>회원수</th></tr></thead>
                <tbody id="clubRows"><tr><td colspan="2" class="empty">불러오는 중...</td></tr></tbody>
            </table>
            <div class="actions">
                <button class="primary" type="button" onclick="location.href='/clubs/new'">단체 생성</button>
            </div>
        </section>
    `);
    document.getElementById('clubSearch').addEventListener('click', () => loadClubs(value('clubKeyword')));
    await loadClubs();
}

//단체 검색했을 때 목록
async function loadClubs(keyword = '') {
    const rows = document.getElementById('clubRows');
    try {
        const clubs = await api(keyword ? `/api/clubs/search?keyword=${encodeURIComponent(keyword)}` : '/api/clubs');
        rows.innerHTML = clubs.length ? clubs.map(club => html`
            <tr>
                <td><a class="post-title-link" href="/clubs/${club.clubId}">${escapeHtml(club.clubName)}</a></td>
                <td style="text-align:center">${club.headcount ?? 0}</td>
            </tr>
        `).join('') : '<tr><td colspan="2" class="empty">단체가 없습니다.</td></tr>';
    } catch (error) {
        rows.innerHTML = '<tr><td colspan="2" class="empty">단체 목록을 불러오지 못했습니다.</td></tr>';
    }
}

//단체 생성 화면
function renderClubForm() {
    if (!requireLogin()) return;
    setApp(html`
        <section class="wide-panel">
            <h1 class="sub-title">단체 생성</h1>
            <div class="form-row"><label>단체명</label><input class="field" id="clubName"></div>
            <div class="form-row"><label>설명</label><textarea id="clubDescription"></textarea></div>
            <div class="actions">
                <button class="secondary" type="button" onclick="location.href='/clubs'">목록 보기</button>
                <button class="primary" id="clubSubmit" type="button">생성</button>
            </div>
        </section>
    `);
    document.getElementById('clubSubmit').addEventListener('click', createClub);
}

//단체 생성 메소드
async function createClub() {
    try {
        const clubId = await api(`/api/clubs?leaderId=${encodeURIComponent(state.memberId)}`, {
            method: 'POST',
            body: JSON.stringify({ clubName: value('clubName'), description: value('clubDescription') })
        });
        location.href = `/clubs/${clubId}`;
    } catch (error) {
        if (error.message && error.message.includes('이미')) {
            alert('이미 단체에 가입되어 있습니다.');
            location.href = '/clubs';
        } else {
            alert(error.message || '단체 생성에 실패했습니다.');
        }
    }
}

//단체 상세 조회
async function renderClubDetail(match) {
    const clubId = match[1];
    setApp('<section class="wide-panel"><p class="empty">불러오는 중...</p></section>');
    try {
        const club = await api(`/api/clubs/${clubId}`);

        //멤버 목록 데이터 받아오기
        let isJoined = false;
        let memberListHtml = '<p class="empty">가입된 멤버가 없습니다.</p>';

        try {
            //로그인 여부와 상관없이 무조건 해당 단체의 멤버 목록 api를 호출
            const members = await api(`/api/clubs/${clubId}/members`);

            //로그인한 사용자라면, 해당 사용자가 이 단체에 가입되어 있는지 확인
            if (state.memberId && members) {
                isJoined = members.some(m => m.memberId === state.memberId);
            }

            //백엔드에서 받아온 멤버 데이터를 그대로 화면에 출력
            if (members && members.length > 0) {
                memberListHtml = html`
                    <div class="club-member-tags">
                        ${members.map(m => html`<span class="member-tag">${escapeHtml(m.name || m.memberName)}</span>`).join(', ')}
                    </div>`;
            }
        } catch (error) {
            console.error('멤버 목록을 불러오지 못했습니다.', error);
            memberListHtml = '<p class="empty">멤버 목록을 불러오지 못했습니다.</p>'
        }

        //권한 및 가입 여부에 따른 버튼 분기 처리
        let actionButton = '';

        //1. 단체장인 경우 (단체 삭제 버튼)
        if (state.memberId === club.leaderMemberId) {
            actionButton = html`<button class="danger" id="clubDelete" type="button">단체 삭제</button>`;
        }

        //2. 이미 해당 단체에 소속되어 있는 회원인 경우 (단체 탈퇴 버튼)
        else if (isJoined) {
            actionButton = html`<button class="secondary" id="clubLeave" type="button">단체 탈퇴</button>`;
        }

        //3. 소속되지 않은 일반 사용자인 경우
        else {
            actionButton = html`<button class="primary" id="clubJoin" type="button">단체 가입</button>`;
        }


        setApp(html`
            <section class="wide-panel">
                <div class="detail-table">
                    <div class="detail-row"><div class="detail-label">단체명</div><div class="detail-value">${escapeHtml(club.clubName)}</div></div>
                    <div class="detail-row"><div class="detail-label">회원수</div><div class="detail-value">${club.headcount ?? 0}</div></div>
                    <div class="detail-row"><div class="detail-label">생성일</div><div class="detail-value">${escapeHtml(club.createDate || '')}</div></div>
                    <div class="detail-row"><div class="detail-label">설명</div><div class="detail-value">${escapeHtml(club.description || '')}</div></div>
                    <div class="detail-row"><div class="detail-label">회원</div><div class="detail-value">${memberListHtml}</div></div>
                </div>
                <div class="actions">
                    ${actionButton}
                    <button class="secondary" type="button" onclick="location.href='/clubs'">목록 보기</button>
                </div>
            </section>
        `);

        //화면에 렌더링된 버튼을 찾아 이벤트 리스너 바인딩
        if (document.getElementById('clubJoin')) {
            document.getElementById('clubJoin').addEventListener('click', () => joinClub(clubId));
        }
        if (document.getElementById('clubLeave')) {
            document.getElementById('clubLeave').addEventListener('click', () => leaveClub(clubId));
        }
        if (document.getElementById('clubDelete')) {
            document.getElementById('clubDelete').addEventListener('click', () => deleteClub(clubId));
        }

    } catch (error) {
        setApp('<section class="wide-panel"><p class="empty">단체 정보를 불러오지 못했습니다.</p></section>');
    }
}

//단체 가입 기능 연결
async function joinClub(clubId) {
    if (!requireLogin()) return;
    if (!confirm('이 단체에 가입하시겠습니까?')) return;

    try {
        await api(`/api/clubs/${clubId}/join-requests`, {
            method: 'POST'
        });
        alert('단체에 가입되었습니다.');
        location.reload();
    } catch (error) {
        if (error.message && error.message.includes('이미')) {
            alert('이미 단체에 가입되어 있습니다.');
            location.href = '/clubs';
        } else {
            alert(error.message || '단체 생성에 실패했습니다.');
        }
    }
}

// 단체 탈퇴 기능 연결
async function leaveClub(clubId) {
    if (!confirm('이 단체를 탈퇴하시겠습니까?')) return;

    try {
        await api(`/api/clubs/${clubId}/members/me`, {
            method: 'DELETE'
        });
        alert('단체에서 탈퇴되었습니다.');
        location.href = '/clubs';
    } catch (error) {
        alert(error.message || '단체 탈퇴에 실패했습니다.');
    }
}

//단체 삭제 기능 연결
async function deleteClub(clubId) {
    if (!confirm('이 단체를 삭제하시겠습니까?\n삭제 시 소속된 모든 팀원이 탈퇴처리 됩니다.')) return;

    try {
        await api(`/api/clubs/${clubId}`, {
            method: 'DELETE'
        });
        alert('단체가 삭제되었습니다.');
        location.href = '/clubs';
    } catch (error) {
        alert(error.message || '단체 삭제에 실패했습니다.');
    }
}
