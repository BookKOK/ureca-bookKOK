let editingField = null;
	// "name" | "phone" | "email" | null

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

					    <td>
					        ${editingField === "name"
					            ? html`<input id="nameInput" value="${escapeHtml(state.name || '')}">`
					            : escapeHtml(state.name || '')
					        }
					    </td>

					    <td>
					        ${editingField === "name"
					            ? html`
					                <button class="mini-btn" id="saveName">저장</button>
					                <button class="mini-btn" id="cancelEdit">취소</button>
					            `
					            : html`
					                <button class="mini-btn" id="editName">수정</button>
					            `
					        }
					    </td>
					</tr>

					<tr>
					    <th>전화번호</th>

					    <td>
					        ${editingField === "phone"
					            ? html`
					                <input id="phoneInput"
					                    value="${formatPhoneNumber(state.phoneNumber || '')}">
					            `
					            : escapeHtml(formatPhoneNumber(state.phoneNumber || ''))
					        }
					    </td>

					    <td>
					        ${editingField === "phone"
					            ? html`
					                <button class="mini-btn" id="savePhone">저장</button>
					                <button class="mini-btn" id="cancelEdit">취소</button>
					            `
					            : html`
					                <button class="mini-btn" id="editPhone">수정</button>
					            `
					        }
					    </td>
					</tr>

					<tr>
					    <th>이메일</th>
					    <td>${escapeHtml(state.email || '')}</td>
					    <td><button class="mini-btn" id="editEmail">수정</button></td>
					</tr>

                    <tr>
                        <th>클럽</th>
                        <td>${escapeHtml(state.clubName || '없음')}</td>
						<td></td>
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
                        <td></td>
                        <td>
                            <button class="mini-btn" id="openPasswordChange">
                                변경
                            </button>
                        </td>
                    </tr>
                </table>

            </div>

        </section>
    `);
	
	const params = new URLSearchParams(location.search);
	const tab = params.get("tab");

	if (tab === "login") {
	    showLoginTab();
	} else {
	    showProfileTab();
	}

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
	
	document.getElementById("openPasswordChange").onclick = () => {
	    location.href = "/mypage/password";
	};
	
	const editNameBtn = document.getElementById('editName');
	if (editNameBtn) {
	    editNameBtn.onclick = () => {
	        editingField = "name";
	        renderMyPage();
	    };
	}
	const editPhoneBtn = document.getElementById('editPhone');
		if (editPhoneBtn) {
		    editPhoneBtn.onclick = () => {
		        editingField = "phone";
		        renderMyPage();
		    };
		}
	
	document.addEventListener("click", async (e) => {
	    if (!e.target.id.startsWith("save")) return;

	    const type = e.target.id.replace("save", "");

	    if (type === "Name") {
	        state.name = document.getElementById("nameInput").value;
	    }

	    if (type === "Email") {
	        state.email = document.getElementById("emailInput").value;
	    }

		if (type === "Phone") {
		    state.phoneNumber = normalizePhone(
		        document.getElementById("phoneInput").value
		    );
		}


	    editingField = null;

	    await api("/api/members/update", {
	        method: "PUT",
	        body: JSON.stringify(state)
	    });

	    renderMyPage();
	});
	
	document.addEventListener("click", (e) => {
	    if (e.target.id === "cancelEdit") {
	        editingField = null;
	        renderMyPage();
	    }
	});
	
    /*document.getElementById('openPasswordChange').onclick = () => {
        document.getElementById('passwordBox').classList.toggle('hidden');
    };

    // 비밀번호 변경
    document.getElementById('passwordSubmit')
        .addEventListener('click', resetPassword);
	
	["currentPassword", "newPassword", "newPasswordCheck"].forEach(id => {
	    document.getElementById(id).addEventListener("keydown", e => {
	        if (e.key === "Enter") {
	            e.preventDefault();   // 엔터 기본 동작 방지
	            resetPassword();
	        }
	    });
	});*/
	document.getElementById('tabProfile').onclick = showProfileTab;
	document.getElementById('tabLogin').onclick = showLoginTab;
	
	// document.getElementById('editName').onclick = showNotReady;
	// document.getElementById('editPhone').onclick = showNotReady;
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

/*function formatPhoneNumber(phone) {
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
}*/

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

	data.phoneNumber = normalizePhone(data.phoneNumber);
	
    Object.assign(state, data);

    renderMyPage();
}

