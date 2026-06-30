function renderMyPage() {
    if (!requireLogin()) return;
    setApp(html`
        <section class="wide-panel">
            <h1 class="sub-title">회원 정보 수정</h1>
            <div class="form-row"><label>아이디</label><input class="field" value="${escapeHtml(state.memberId)}" disabled></div>
            <div class="form-row"><label>비밀번호</label><input class="field" id="currentPassword" type="password" placeholder="현재 비밀번호"></div>
            <div class="form-row"><label>새 비밀번호</label><input class="field" id="newPassword" type="password"></div>
            <div class="actions">
                <button class="primary" id="passwordSubmit" type="button">비밀번호 변경</button>
            </div>
            <p class="notice">이름/전화번호/프로필 변경 API는 아직 없어 화면만 준비했습니다.</p>
        </section>
    `);
    document.getElementById('passwordSubmit').addEventListener('click', resetPassword);
}

async function resetPassword() {
    try {
        await api('/api/members/password/reset', {
            method: 'POST',
            body: JSON.stringify({
                memberId: state.memberId,
                currentPassword: value('currentPassword'),
                newPassword: value('newPassword')
            })
        });
        alert('비밀번호가 변경되었습니다.');
    } catch (error) {
        alert('비밀번호 변경에 실패했습니다.');
    }
}
