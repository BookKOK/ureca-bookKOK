function renderLogin() {
    hideModal();
    clearAuth();
	setApp(html`
	<section class="login-panel">
	    <h1 class="page-title">로그인</h1>

	    <form id="loginForm">
	        <div class="login-form">
	            <div class="login-inputs">
	                <div class="form-row">
	                    <input class="field"
	                           id="loginId"
	                           placeholder="아이디">
	                </div>

	                <div class="form-row">
	                    <input class="field"
	                           id="loginPassword"
	                           type="password"
	                           placeholder="비밀번호">
	                </div>
	            </div>

	            <button class="primary login-submit"
	                    id="loginSubmit"
	                    type="submit">
	                로그인
	            </button>
	        </div>

	        <p class="login-error" id="loginMessage" hidden></p>
	    </form>

	    <div class="link-row">
	        <a href="/signup">회원가입</a>
			<span>|</span>
			<a href="#" onclick="showModal('아직 기능구현 전입니다.'); return false;">아이디 찾기</a>
			<span>|</span>
			<a href="#" onclick="showModal('아직 기능구현 전입니다.'); return false;">비밀번호 찾기</a>
	    </div>

	    <hr>

	    <img id="kakaoLogin"
	         src="/images/kakao_login_medium_narrow.png"
	         alt="카카오 로그인">

	</section>
	`);
	document.getElementById('loginForm').addEventListener('submit', (e) => {
	    e.preventDefault();
	    login();
	});
	document.getElementById("kakaoLogin").addEventListener("click", kakaoLogin);
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
        message.textContent = '아이디 또는 비밀번호가 잘못되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.';
    }
}

function kakaoLogin() {
    location.href =
        "https://kauth.kakao.com/oauth/authorize"
        + "?client_id=62f4228bd2b51bf2f39dec36373333e0"
        + "&redirect_uri=http://localhost:8080/login/kakao/auth-code"
        + "&response_type=code";
}

function renderSignup() {
    hideModal();
	setApp(html`
	<section class="signup-panel">

	    <h1 class="page-title">회원가입</h1>

	    <!-- 계정 정보 -->
	    <div class="signup-box">

	        <div class="signup-row">
	            <input class="field"
	                   id="signupId"
	                   placeholder="아이디">
	        </div>

	        <div class="signup-row">
	            <input class="field"
	                   id="signupPassword"
	                   type="password"
	                   placeholder="비밀번호">
	        </div>

	        <div class="signup-row">
	            <input class="field"
	                   id="signupPasswordCheck"
	                   type="password"
	                   placeholder="비밀번호 확인">
	        </div>

	    </div>

	    <!-- 개인정보 -->
	    <div class="signup-box">

	        <div class="signup-row">
	            <input class="field"
	                   id="signupName"
	                   placeholder="이름">
	        </div>

	        <div class="signup-row">
	            <input class="field"
	                   id="signupPhone"
	                   placeholder="전화번호">
	        </div>

	        <div class="signup-row">
	            <input class="field"
	                   id="signupEmail"
	                   placeholder="이메일">
	        </div>

	    </div>

	    <p class="signup-error"
	       id="signupMessage"
	       hidden></p>

	    <div class="signup-buttons">
	        <button class="secondary"
	                onclick="location.href='/login'">
	            취소
	        </button>

	        <button class="primary"
	                id="signupSubmit">
	            회원가입
	        </button>
	    </div>

	</section>
	`);
    document.getElementById('signupSubmit').addEventListener('click', signup);
}

async function signup() {
    const message = document.getElementById('signupMessage');
	
	const id = value('signupId').trim();
    const password = value('signupPassword').trim();
	const passwordCheck = value('signupPasswordCheck').trim();
    const name = value('signupName').trim();
    const email = value('signupEmail').trim();
    const phone = value('signupPhone').trim();

    if (!id || !password || !passwordCheck || !name || !email || !phone) {
		message.hidden = false;
		message.textContent = '모든 필수 항목을 입력해 주세요.';
        return;
    }
		
    try {
		
        await api('/api/members/signup', {
            method: 'POST',
            body: JSON.stringify({
                memberId: value('signupId'),
                password: value('signupPassword'),
				passwordCheck: value('signupPasswordCheck'),
                name: value('signupName'),
                email: value('signupEmail'),
                phoneNumber: value('signupPhone')
            })
        });
        alert('회원가입이 완료되었습니다.');
        location.href = '/login';
    } catch (error) {
		message.hidden = false;
	    message.textContent = error.message;
    }
}
