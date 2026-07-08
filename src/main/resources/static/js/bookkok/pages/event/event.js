const COURT_LIST = [1, 2, 3, 4, 5];
// 1. 팝업창을 띄우는 함수 (입력 필드 추가)
function renderEventModal() {
    const courtOptions = COURT_LIST.map(num =>
        `<option value="${num}">${num}번 코트</option>`).join('');
    const timeOptions = Array.from({ length: 10 }, (_, i) => i + 9).map(hour =>
        `<option value="${hour}:00">${hour}:00</option>`).join('');
    console.log("renderEventModal 함수가 실행되었습니다!");
    const modalHtml = `
        <div class="modal-backdrop" id="eventModalBackdrop">
            <div class="modal">
                <button class="modal-close" onclick="closeModal()">×</button>
                <h3>행사 등록</h3>
                <div class="form-row">
                    <label>행사명</label>
                    <input type="text" id="eventName" class="field" placeholder="행사명 입력">
                </div>
                <div class="form-row">
                    <label>기간</label>
                    <input type="date" id="startDate" class="field"> ~ <input type="date" id="endDate" class="field">
                </div>
                <div class="form-row">
                    <label>시간</label>
                    <select id="startTime" class="field">${timeOptions}</select> ~ 
                    <select id="endTime" class="field">${timeOptions}</select>
                </div>
                <div class="form-row">
                    <label>코트</label>
                    <select id="court">
                        ${courtOptions}
                    </select><br>
                </div>
                <div class="actions">
                    <button class="secondary" type="button" onclick="closeModal()">취소</button>
                    <button class="primary" type="button" onclick="submitEvent()">등록 완료</button>
                </div>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', modalHtml);
}

// 2. 입력된 데이터를 백엔드로 전송하는 함수
async function submitEvent() {
    // 폼에서 데이터 가져오기
    const eventData = {
        name: document.getElementById('eventName').value,
        startDate: document.getElementById('startDate').value,
        endDate: document.getElementById('endDate').value,
        startTime: document.getElementById('startTime').value,
        endTime: document.getElementById('endTime').value,
        court: document.getElementById('court').value,
        clubId: 1 // 관리자용 가상 클럽 ID 고정
    };

    // 데이터가 비어있는지 간단히 검증
    if (!eventData.name || !eventData.startDate || !eventData.endDate) {
        alert("모든 항목을 입력해주세요.");
        return;
    }

    try {
        // 백엔드로 데이터 전송
        await api('/api/events', {
            method: 'POST',
            body: JSON.stringify(eventData)
        });

        alert('행사가 등록되었습니다.');
        closeModal(); // 팝업 닫기
    } catch (error) {
        alert('등록 실패: ' + error.message);
    }
}

// 팝업 닫기 함수
function closeModal() {
    // 팝업 전체를 감싸고 있는 배경(Backdrop)의 ID를 정확히 찾아야 합니다.
    const backdrop = document.getElementById('eventModalBackdrop');
    if (backdrop) backdrop.remove();
}