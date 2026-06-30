function renderLogin() {
    hideModal();
    clearAuth();
    setApp(html`
        <section class="center-panel">
            <h1 class="page-title">로그인</h1>
            <div class="form-row">
                <label for="loginId">아이디</label>
                <input class="field" id="loginId" placeholder="아이디">
            </div>
            <div class="form-row">
                <label for="loginPassword">비밀번호</label>
                <input class="field" id="loginPassword" type="password" placeholder="비밀번호">
            </div>
            <div class="actions">
                <button class="primary" id="loginSubmit" type="button">로그인</button>
            </div>
            <div class="link-row">
                <a href="/signup">회원가입</a>
                <a href="/home">둘러보기</a>
            </div>
            <button class="secondary" type="button" onclick="alert('카카오 로그인은 아직 연결되지 않았습니다.')">카카오 아이디로 로그인</button>
            <p class="notice" id="loginMessage" hidden></p>
        </section>
    `);
    document.getElementById('loginSubmit').addEventListener('click', login);
}

async function login() {
    const message = document.getElementById('loginMessage');
    try {
        const memberId = value('loginId');
        const body = await api('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ memberId, password: value('loginPassword') })
        });
        saveLogin(body.accessToken, memberId);
        location.href = '/home';
    } catch (error) {
        message.hidden = false;
        message.textContent = '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.';
    }
}

function renderSignup() {
    hideModal();
    setApp(html`
        <section class="wide-panel">
            <h1 class="sub-title">회원가입</h1>
            <button class="secondary small" type="button" onclick="alert('카카오 회원가입은 아직 연결되지 않았습니다.')">카카오 아이디로 회원가입</button>
            <hr>
            <div class="form-row"><label for="signupId">아이디</label><input class="field" id="signupId"></div>
            <div class="form-row"><label for="signupPassword">비밀번호</label><input class="field" id="signupPassword" type="password"></div>
            <div class="form-row"><label for="signupName">이름</label><input class="field" id="signupName"></div>
            <div class="form-row"><label for="signupPhone">전화번호</label><input class="field" id="signupPhone"></div>
            <div class="form-row"><label for="signupEmail">이메일</label><input class="field" id="signupEmail"></div>
            <div class="actions">
                <button class="secondary" type="button" onclick="location.href='/login'">취소</button>
                <button class="primary" id="signupSubmit" type="button">회원가입</button>
            </div>
            <p class="notice" id="signupMessage" hidden></p>
        </section>
    `);
    document.getElementById('signupSubmit').addEventListener('click', signup);
}

async function signup() {
    const message = document.getElementById('signupMessage');
    try {
        await api('/api/members/signup', {
            method: 'POST',
            body: JSON.stringify({
                memberId: value('signupId'),
                password: value('signupPassword'),
                name: value('signupName'),
                email: value('signupEmail'),
                phoneNumber: value('signupPhone')
            })
        });
        alert('회원가입이 완료되었습니다.');
        location.href = '/login';
    } catch (error) {
        message.hidden = false;
        message.textContent = '회원가입에 실패했습니다. 입력값을 확인해주세요.';
    }
}
