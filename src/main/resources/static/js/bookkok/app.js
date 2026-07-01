const app = document.getElementById('app');
const modal = document.getElementById('modal');
const modalMessage = document.getElementById('modalMessage');
const authButton = document.getElementById('authButton');
const state = {
    token: localStorage.getItem('bookkokAccessToken') || '',
    memberId: localStorage.getItem('bookkokMemberId') || '',
    role: localStorage.getItem('bookkokRole') || '',
    selectedDate: '',
    selectedTime: '',
    selectedCourt: ''
};


document.getElementById('modalClose').addEventListener('click', hideModal);
document.getElementById('modalOk').addEventListener('click', () => {
    hideModal();
    location.href = '/login';
});
authButton.addEventListener('click', () => {
    if (isLoggedIn()) {
        clearAuth();
        location.href = '/login';
        return;
    }
    location.href = '/login';
});

function refreshAuthFromStorage() {
    state.token = localStorage.getItem('bookkokAccessToken') || '';
    state.memberId = localStorage.getItem('bookkokMemberId') || '';
    state.role = localStorage.getItem('bookkokRole') || '';
}

function applyAuthUi() {
    document.body.classList.toggle('is-admin', isAdmin());
    authButton.textContent = isLoggedIn() ? `${state.memberId || '회원'} 로그아웃` : '로그인';
}

function isLoggedIn() {
    return Boolean(state.token);
}

function isAdmin() {
    return state.role.includes('ADMIN');
}

function clearAuth() {
    state.token = '';
    state.memberId = '';
    state.role = '';
    localStorage.removeItem('bookkokAccessToken');
    localStorage.removeItem('bookkokMemberId');
    localStorage.removeItem('bookkokRole');
    applyAuthUi();
}

function requireLogin() {
    if (isLoggedIn()) {
        return true;
    }
    showModal('로그인이 필요합니다.');
    return false;
}

function showModal(message) {
    modalMessage.textContent = message;
    modal.hidden = false;
}

function hideModal() {
    modal.hidden = true;
}

function html(strings, ...values) {
    return strings.reduce((acc, string, index) => acc + string + (values[index] ?? ''), '');
}

function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

function value(id) {
    return document.getElementById(id)?.value || '';
}

function setApp(markup) {
    hideModal();
    app.innerHTML = markup;
}

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

    if (response.status === 401  || response.status === 403) {
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
		
    /*if (!response.ok) {
		const message =
		        typeof body === 'string'
		            ? body
		            : body?.message || JSON.stringify(body);

		    throw new Error(message || `요청 실패 (${response.status})`);
        const message = typeof body === 'string' ? body : JSON.stringify(body);
        throw new Error(message || `요청 실패 (${response.status})`);
    }*/
    return body;
}

function decodeToken(token) {
    try {
        const payload = token.split('.')[1];
        return JSON.parse(decodeURIComponent(escape(atob(payload.replace(/-/g, '+').replace(/_/g, '/')))));
    } catch (error) {
        return {};
    }
}

function saveLogin(token, fallbackMemberId) {
    const payload = decodeToken(token);
    state.token = token;
    state.memberId = payload.sub || fallbackMemberId;
    state.role = payload.role || '';
    localStorage.setItem('bookkokAccessToken', state.token);
    localStorage.setItem('bookkokMemberId', state.memberId);
    localStorage.setItem('bookkokRole', state.role);
}

