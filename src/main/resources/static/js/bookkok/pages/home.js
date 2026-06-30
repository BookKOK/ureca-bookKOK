function renderHome() {
    setApp(html`
        <section class="wide-panel">
            <h1 class="page-title">BookKOK</h1>
            <div class="search-box">
                <p><strong>${escapeHtml(state.memberId || '방문자')}</strong>님, 사용할 메뉴를 선택하세요.</p>
                ${isAdmin() ? '<p class="notice">관리자 계정입니다. 상단 관리자 메뉴가 활성화되었습니다.</p>' : ''}
            </div>
            <div class="admin-grid">
                <a class="admin-card" href="/reservations"><h2>시설 예약</h2><p>날짜, 시간, 코트를 선택해 예약합니다.</p></a>
                <a class="admin-card" href="/clubs"><h2>단체 관리</h2><p>단체 목록을 보고 새 단체를 생성합니다.</p></a>
                <a class="admin-card" href="/board"><h2>게시판</h2><p>공지와 게시글을 확인하고 작성합니다.</p></a>
                <a class="admin-card" href="/mypage"><h2>마이페이지</h2><p>내 정보와 예약 관련 기능을 확인합니다.</p></a>
                ${isAdmin() ? '<a class="admin-card" href="/admin"><h2>관리자</h2><p>회원, 예약, 카테고리를 관리합니다.</p></a>' : ''}
            </div>
        </section>
    `);
}
