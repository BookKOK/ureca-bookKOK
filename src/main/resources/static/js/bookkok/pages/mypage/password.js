function renderPasswordPage() {
    if (!requireLogin()) return;

	setApp(html`
	    <section class="login-panel">

	        <h1 class="page-title">비밀번호 변경</h1>

	        <div class="password-box">
	            <input class="field"
	                   id="currentPassword"
	                   type="password"
	                   placeholder="현재 비밀번호">

	            <input class="field"
	                   id="newPassword"
	                   type="password"
	                   placeholder="새 비밀번호">

	            <input class="field"
	                   id="newPasswordCheck"
	                   type="password"
	                   placeholder="새 비밀번호 확인">

	            <p id="passwordMessage"
	               class="signup-error"
	               hidden></p>

			   <div class="password-buttons">
			       <button class="secondary" id="passwordCancel">
			           취소
			       </button>

			       <button class="primary" id="passwordSubmit">
			           변경하기
			       </button>
			   </div>
	        </div>

	    </section>
	`);

    document.getElementById("passwordCancel").onclick = () => {
		location.href = "/mypage?tab=login";
    };

    document.getElementById("passwordSubmit")
        .addEventListener("click", resetPassword);

	
    document
        .querySelectorAll("#currentPassword,#newPassword,#newPasswordCheck")
        .forEach(input =>
            input.addEventListener("keydown", e => {
                if (e.key === "Enter") {
                    resetPassword();
                }
            })
        );
}

async function resetPassword() {

	const message = document.getElementById("passwordMessage");

    if (!value("currentPassword") ||
        !value("newPassword") ||
        !value("newPasswordCheck")) {

		message.hidden = false;
        message.textContent = "모든 항목을 입력해주세요.";
        return;
    }

    try {
		await api("/api/members/password/reset", {
		    method: "POST",
		    body: JSON.stringify({
		        memberId: state.memberId,
		        currentPassword: value("currentPassword"),
		        newPassword: value("newPassword"),
		        newPasswordCheck: value("newPasswordCheck")
		    })
		});

        alert("비밀번호가 변경되었습니다.");

        document.getElementById("passwordBox").classList.add("hidden");
		location.href = "/mypage?tab=login";
		
    } catch (error) {
		message.hidden = false;
	    message.textContent = error.message;

    }
}