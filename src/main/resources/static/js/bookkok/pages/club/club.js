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

async function createClub() {
    try {
        const clubId = await api(`/api/clubs?leaderId=${encodeURIComponent(state.memberId)}`, {
            method: 'POST',
            body: JSON.stringify({ clubName: value('clubName'), description: value('clubDescription') })
        });
        location.href = `/clubs/${clubId}`;
    } catch (error) {
        alert('단체 생성에 실패했습니다.');
    }
}

async function renderClubDetail(match) {
    const clubId = match[1];
    setApp('<section class="wide-panel"><p class="empty">불러오는 중...</p></section>');
    try {
        const club = await api(`/api/clubs/${clubId}`);
        setApp(html`
            <section class="wide-panel">
                <div class="detail-table">
                    <div class="detail-row"><div class="detail-label">단체명</div><div class="detail-value">${escapeHtml(club.clubName)}</div></div>
                    <div class="detail-row"><div class="detail-label">회원수</div><div class="detail-value">${club.headcount ?? 0}</div></div>
                    <div class="detail-row"><div class="detail-label">생성일</div><div class="detail-value">${escapeHtml(club.createDate || '')}</div></div>
                    <div class="detail-row"><div class="detail-label">설명</div><div class="detail-value">${escapeHtml(club.description || '')}</div></div>
                </div>
                <div class="actions">
                    <button class="primary" type="button" onclick="alert('가입 API가 아직 없어 화면만 준비되었습니다.')">단체 가입하기</button>
                    <button class="secondary" type="button" onclick="location.href='/clubs'">목록 보기</button>
                </div>
            </section>
        `);
    } catch (error) {
        setApp('<section class="wide-panel"><p class="empty">단체 정보를 불러오지 못했습니다.</p></section>');
    }
}
