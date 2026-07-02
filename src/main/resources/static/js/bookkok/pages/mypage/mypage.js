function renderMyPage() {
    if (!requireLogin()) return;

	if (!state.email) {
        loadData();   // 최초 1번만
        return;
    }
	
    setApp(html`
        <section class="mypage-container">

            <h1 class="page-title">마이페이지</h1>

            <!-- 탭 -->
            <div class="mypage-tabs">
                <button class="tab active" id="tabProfile">내 프로필</button>
                <button class="tab" id="tabLogin">로그인 정보</button>
            </div>

            <!-- 내 프로필 -->
            <div class="card" id="profileSection">
                <h2>내 프로필</h2>

                <table class="info-table">
					<tr>
					    <th>이름</th>
					    <td>${escapeHtml(state.name || '')}</td>
					    <td><button class="mini-btn" id="editName">수정</button></td>
					</tr>

					<tr>
					    <th>전화번호</th>
					    <td>${escapeHtml(formatPhoneNumber(state.phoneNumber))}</td>
					    <td><button class="mini-btn" id="editPhone">수정</button></td>
					</tr>

					<tr>
					    <th>이메일</th>
					    <td>${escapeHtml(state.email || '')}</td>
					    <td><button class="mini-btn" id="editEmail">수정</button></td>
					</tr>

                    <tr>
                        <th>클럽</th>
                        <td>${escapeHtml(state.clubName || '없음')}</td>
                    </tr>

                    <tr>
                        <th>가입일</th>
                        <td>${escapeHtml(formatDate(state.regDate))}</td>
                        <td></td>
                    </tr>
                </table>
            </div>

            <!-- 로그인 정보 -->
            <div class="card hidden" id="loginSection">
                <h2>로그인 정보</h2>

                <table class="info-table">
                    <tr>
                        <th>아이디</th>
                        <td>${escapeHtml(state.memberId)}</td>
                        <td></td>
                    </tr>

                    <tr>
                        <th>비밀번호</th>
                        <td>********</td>
                        <td>
                            <button class="mini-btn" id="openPasswordChange">
                                변경
                            </button>
                        </td>
                    </tr>
                </table>

                <!-- 비밀번호 변경 폼 -->
                <div class="password-box hidden" id="passwordBox">
                    <input id="currentPassword" type="password" placeholder="현재 비밀번호" />
                    <input id="newPassword" type="password" placeholder="새 비밀번호" />

                    <button class="primary" id="passwordSubmit">
                        변경하기
                    </button>
                </div>
            </div>

        </section>
    `);

    // 탭 전환
    document.getElementById('tabProfile').onclick = () => {
        document.getElementById('profileSection').classList.remove('hidden');
        document.getElementById('loginSection').classList.add('hidden');
    };

    document.getElementById('tabLogin').onclick = () => {
        document.getElementById('profileSection').classList.add('hidden');
        document.getElementById('loginSection').classList.remove('hidden');
    };

    // 비밀번호 변경 열기
    document.getElementById('openPasswordChange').onclick = () => {
        document.getElementById('passwordBox').classList.toggle('hidden');
    };

    // 비밀번호 변경
    document.getElementById('passwordSubmit')
        .addEventListener('click', resetPassword);
		
	document.getElementById('tabProfile').onclick = showProfileTab;
	document.getElementById('tabLogin').onclick = showLoginTab;
	
	document.getElementById('editName').onclick = showNotReady;
	document.getElementById('editPhone').onclick = showNotReady;
	document.getElementById('editEmail').onclick = showNotReady;
}

function showProfileTab() {
    document.getElementById('profileSection').classList.remove('hidden');
    document.getElementById('loginSection').classList.add('hidden');

    document.getElementById('tabProfile').classList.add('active');
    document.getElementById('tabLogin').classList.remove('active');
}

function showNotReady() {
    showModal("아직 기능 구현 전입니다.");
}

function showLoginTab() {
    document.getElementById('profileSection').classList.add('hidden');
    document.getElementById('loginSection').classList.remove('hidden');

    document.getElementById('tabProfile').classList.remove('active');
    document.getElementById('tabLogin').classList.add('active');
}

function formatPhoneNumber(phone) {
    if (!phone) return '';

    // +82 -> 0으로 변경
    phone = phone.replace('+82', '0');

    // 숫자만 추출
    phone = phone.replace(/\D/g, '');

    if (phone.length === 11) {
        return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    }

    if (phone.length === 10) {
        return phone.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
    }

    return phone;
}

function formatDate(date) {
    if (!date) return '';

    const d = new Date(date);

    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');

    return `${year}.${month}.${day}`;
}

async function loadData() {
    const data = await api('/api/members/me');

    Object.assign(state, data);

    renderMyPage();
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
