function renderAdmin() {
    hideModal();
    if (!isLoggedIn()) {
        clearAuth();
        location.replace('/login');
        return;
    }
    if (!isAdmin()) {
        alert('관리자만 사용할 수 있습니다.');
        location.replace('/home');
        return;
    }
    setApp(html`
        <section class="wide-panel">
            <h1 class="sub-title">관리자 기능</h1>
            <div class="admin-grid">
                <div class="admin-card">
                    <h2>회원 관리</h2>
                    <input class="field" id="adminMemberId" placeholder="회원 ID">
                    <div class="actions">
                        <button class="secondary small" id="adminUsers" type="button">목록</button>
                        <button class="primary small" id="adminUserBlock" type="button">차단</button>
                        <button class="secondary small" id="adminUserUnblock" type="button">해제</button>
                    </div>
                </div>
                <div class="admin-card">
                    <h2>예약 관리</h2>
                    <input class="field" id="adminClubName" placeholder="단체명 검색">
                    <div class="actions">
                        <button class="secondary small" id="adminReservations" type="button">전체</button>
                        <button class="primary small" id="adminReservationSearch" type="button">검색</button>
                    </div>
                </div>
                <div class="admin-card">
                    <h2>카테고리 관리</h2>
                    <input class="field" id="adminCategoryId" placeholder="카테고리 ID">
                    <input class="field" id="adminCategoryName" placeholder="카테고리명">
                    <div class="actions">
                        <button class="primary small" id="adminCategoryCreate" type="button">생성</button>
                        <button class="secondary small" id="adminCategoryList" type="button">목록</button>
                    </div>
                </div>
            </div>
            <pre class="notice" id="adminResult">결과가 여기에 표시됩니다.</pre>
        </section>
    `);
    bindAdmin();
}

function bindAdmin() {
    const output = document.getElementById('adminResult');
    const show = body => output.textContent = typeof body === 'string' ? body : JSON.stringify(body, null, 2);
    document.getElementById('adminUsers').addEventListener('click', async () => show(await api('/api/admin/users')));
    document.getElementById('adminUserBlock').addEventListener('click', async () => show(await api(`/api/admin/users/${encodeURIComponent(value('adminMemberId'))}/block`, { method: 'PATCH' })));
    document.getElementById('adminUserUnblock').addEventListener('click', async () => show(await api(`/api/admin/users/${encodeURIComponent(value('adminMemberId'))}/unblock`, { method: 'PATCH' })));
    document.getElementById('adminReservations').addEventListener('click', async () => show(await api('/api/admin/reservations')));
    document.getElementById('adminReservationSearch').addEventListener('click', async () => show(await api(`/api/admin/reservations/search?clubName=${encodeURIComponent(value('adminClubName'))}`)));
    document.getElementById('adminCategoryCreate').addEventListener('click', async () => show(await api('/api/admin/category', {
        method: 'POST',
        body: JSON.stringify({ name: value('adminCategoryName') })
    })));
    document.getElementById('adminCategoryList').addEventListener('click', async () => show(await api('/api/admin/category')));
}
