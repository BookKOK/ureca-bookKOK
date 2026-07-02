/*
 * app.js
 *
 * index.html에서 가장 먼저 로드되는 공통 스크립트.
 *
 * 실행 흐름:
 * 1. index.html 로드
 * 2. app.js가 공통 DOM 요소와 로그인 상태 준비
 * 3. 화면별 JS 파일들이 renderLogin(), renderBoard() 같은 함수 등록
 * 4. router.js가 현재 URL에 맞는 render 함수 호출
 */

// index.html의 <main id="app"> 영역.
// 각 화면 render 함수가 setApp()으로 실제 화면 HTML을 넣는 곳.
const app = document.getElementById('app');

// 로그인 필요 안내 등 공통 모달 제어용 DOM 요소.
const modal = document.getElementById('modal');
const modalMessage = document.getElementById('modalMessage');
const authButton = document.getElementById('authButton');

// 프론트에서 공통으로 사용하는 상태 값.
// 페이지 이동 후에도 로그인 상태를 유지하려고 localStorage에서 JWT와 회원 정보 읽어옴.
const state = {
    token: localStorage.getItem('bookkokAccessToken') || '',
    memberId: localStorage.getItem('bookkokMemberId') || '',
    role: localStorage.getItem('bookkokRole') || '',
    selectedDate: '',
    selectedTime: '',
    selectedCourt: ''
};

const params = new URLSearchParams(location.search);
const token = params.get("accessToken");

if (token) {
    saveLogin(token);
    history.replaceState({}, "", "/home"); // 주소창에서 토큰 제거
}

let modalRedirectUrl = null; 

// index.html의 모달 X 버튼 클릭 시 모달만 닫는 담당.
document.getElementById('modalClose').addEventListener('click', hideModal);

// 모달의 확인 버튼 클릭 시 로그인 화면으로 이동.
// 예: 예약 화면에서 비로그인 상태로 날짜를 누름 -> requireLogin() -> 모달 표시
// -> 확인 클릭 -> /login 이동
document.getElementById('modalOk').addEventListener('click', () => {
    hideModal();
	if (modalRedirectUrl) {
	        location.href = modalRedirectUrl; // 경로가 지정되어 있으면 이동
	}
});

// 상단 로그인/로그아웃 버튼 처리 담당.
// 로그인 상태면 토큰 삭제 후 /login 이동.
// 비로그인 상태면 바로 /login 이동.
authButton.addEventListener('click', () => {
    if (isLoggedIn()) {
        clearAuth();
        location.href = '/login';
        return;
    }
    location.href = '/login';
});

// router.js의 boot() 함수에서 호출.
// URL 라우팅 전에 localStorage의 최신 로그인 정보를 state에 다시 반영.
function refreshAuthFromStorage() {
    state.token = localStorage.getItem('bookkokAccessToken') || '';
    state.memberId = localStorage.getItem('bookkokMemberId') || '';
    state.role = localStorage.getItem('bookkokRole') || '';
}

// router.js의 boot() 함수에서 호출.
// 현재 로그인/관리자 상태에 맞춰 상단 UI 갱신.
// 관리자면 body에 is-admin 클래스를 붙여 관리자 메뉴 노출.
function applyAuthUi() {
    document.body.classList.toggle('is-admin', isAdmin());
	if (isLoggedIn()) {
	        authArea.innerHTML = `
	            <a href="/mypage" class="member-link">
	                ${state.memberId || '회원'}님
	            </a>

	            <button id="logoutButton" class="login-link">
	                로그아웃
	            </button>
	        `;

	        document
	            .getElementById('logoutButton')
	            .addEventListener('click', () => {
	                clearAuth();
	                location.href = "/home";
	            });

	    } else {
	        authArea.innerHTML = `
	            <button id="loginButton" class="login-link">
	                로그인
	            </button>
	        `;

	        document
	            .getElementById('loginButton')
	            .addEventListener('click', () => {
	                location.href = "/login";
	            });
	    }
}

// 현재 프론트가 로그인 상태라고 판단하는 기준.
// state.token은 saveLogin() 또는 refreshAuthFromStorage()로 채워짐.
function isLoggedIn() {
    return Boolean(state.token);
}

// JWT payload에서 꺼낸 role 값에 ADMIN이 포함되어 있는지 확인.
// 관리자 메뉴 노출과 /admin 접근 제어에 사용.
function isAdmin() {
    return state.role.includes('ADMIN');
}

// 로그아웃 또는 401 응답 발생 시 호출.
// localStorage와 state를 모두 비워 프론트 로그인 상태 초기화.
function clearAuth() {
    state.token = '';
    state.memberId = '';
    state.role = '';
    localStorage.removeItem('bookkokAccessToken');
    localStorage.removeItem('bookkokMemberId');
    localStorage.removeItem('bookkokRole');
    applyAuthUi();
}

// 로그인 필수 화면/동작에서 먼저 호출하는 가드 함수.
// 로그인 상태면 true 반환, 아니면 모달 표시 후 false 반환.
//
// 예: reservation.js에서 날짜 선택 클릭
// -> requireLogin()
// -> 비로그인 상태면 "로그인이 필요합니다." 모달 표시
function requireLogin() {
    if (isLoggedIn()) {
        return true;
    }
    showModal('로그인이 필요합니다.', '/login');
    return false;
}

// 공통 모달 메시지 설정 후 화면에 표시.
function showModal(message, redirectUrl = null) {
    modalMessage.textContent = message;
	modalRedirectUrl = redirectUrl;
    modal.hidden = false;
}

// 공통 모달 숨김.
function hideModal() {
    modal.hidden = true;
	modalRedirectUrl = null;
}

// 화면별 JS에서 템플릿 문자열을 읽기 좋게 쓰기 위한 헬퍼.
function html(strings, ...values) {
    return strings.reduce((acc, string, index) => acc + string + (values[index] ?? ''), '');
}

// API 응답 데이터를 HTML에 넣을 때 태그가 실행되지 않도록 이스케이프.
function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

// input, textarea 등의 값을 가져오는 공통 헬퍼.
function value(id) {
    return document.getElementById(id)?.value || '';
}

// 화면별 render 함수들이 최종적으로 호출하는 함수.
// index.html의 <main id="app"> 내용을 새로운 화면 HTML로 교체.
//
// 예: router.js에서 renderLogin() 호출
// -> auth.js의 renderLogin()
// -> setApp(로그인 화면 HTML)
// -> index.html의 #app 영역이 로그인 화면으로 변경됨
function setApp(markup) {
    hideModal();
    app.innerHTML = markup;
}

// 모든 REST API 호출에 사용하는 공통 fetch 함수.
// state.token이 있으면 Authorization 헤더에 Bearer 토큰 추가.
//
// 예: board.js에서 api('/api/posts') 호출
// -> 이 함수가 fetch 실행
// -> 응답 JSON 또는 text를 반환
async function api(path, options = {}) {
    const headers = { ...(options.headers || {}) };
    if (options.body && !headers['Content-Type']) {
        headers['Content-Type'] = 'application/json';
    }
    if (state.token) {
        headers.Authorization = `Bearer ${state.token}`;
    }

    const response = await fetch(path, { ...options, headers });
    const contentType = response.headers.get('content-type') || '';
    const body = contentType.includes('application/json') ? await response.json() : await response.text();

    if (response.status === 401 || response.status === 403) {
        clearAuth();
    }

	if (!response.ok) {
		        let errorMessage = '';

		        if (typeof body === 'string') {
		            errorMessage = body;
		        } else if (body && typeof body === 'object') {
		            // 백엔드에서 주로 보내는 에러 필드명들 (message, error, detail 등)을 체크
		            errorMessage = body.message || body.error || body.detail || JSON.stringify(body);
		        }

		        // 최종적으로 추출된 메시지가 없으면 status 코드와 함께 던짐
		        throw new Error(errorMessage || `요청 실패 (${response.status})`);
		    }
    return body;
}

// 로그인 응답으로 받은 JWT access token의 payload 읽기.
// 여기서 sub(memberId), role 값을 꺼내 saveLogin()에서 사용.
function decodeToken(token) {
    try {
        const payload = token.split('.')[1];
        return JSON.parse(decodeURIComponent(escape(atob(payload.replace(/-/g, '+').replace(/_/g, '/')))));
    } catch (error) {
        return {};
    }
}

// 로그인 성공 후 호출.
// JWT와 회원 정보를 state와 localStorage에 저장.
// 이후 router.js가 /home으로 이동하고 applyAuthUi()가 상단 UI 갱신.
function saveLogin(token, fallbackMemberId) {
    const payload = decodeToken(token);
    state.token = token;
    state.memberId = payload.sub || fallbackMemberId;
    state.role = payload.role || '';
    localStorage.setItem('bookkokAccessToken', state.token);
    localStorage.setItem('bookkokMemberId', state.memberId);
    localStorage.setItem('bookkokRole', state.role);
}
