function renderSteps(active) {
    return html`
        <div class="steps">
            ${[1, 2, 3].map(num => `<div class="step ${num === active ? 'active' : ''}"><span>${num}</span></div>`).join('')}
        </div>
        <div class="step-labels"><span>예약 선택</span><span>정보 입력</span><span>예약 확정</span></div>
    `;
}

async function renderReservationStep1() {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth();
    setApp(html`
        ${renderSteps(1)}
        <section class="calendar-layout">
            <div>
                <div class="calendar-head">
                    <button class="month-button" type="button">‹</button>
                    <h1>${year}. ${String(month + 1).padStart(2, '0')}.</h1>
                    <button class="month-button" type="button">›</button>
                </div>
                <div class="calendar" id="calendar"></div>
            </div>
            <aside>
                <div class="choice-panel">
                    <div class="choice-title">시간 선택</div>
                    <div class="time-grid" id="timeGrid"></div>
                </div>
                <div class="choice-panel">
                    <div class="choice-title">코트 선택</div>
                    <div class="court-list" id="courtList"></div>
                </div>
                <div class="actions">
                    <button class="primary" id="reservationNext" type="button">다음</button>
                </div>
            </aside>
        </section>
    `);
    renderCalendar(year, month);
    renderReservationChoices();
    document.getElementById('reservationNext').addEventListener('click', goReservationInfo);
}

function renderCalendar(year, month) {
    const calendar = document.getElementById('calendar');
    const dayNames = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
    const first = new Date(year, month, 1);
    const last = new Date(year, month + 1, 0);
    const cells = dayNames.map(name => `<div class="day-name">${name}</div>`);
    for (let i = 0; i < first.getDay(); i += 1) {
        cells.push('<div class="day-cell muted"></div>');
    }
    for (let day = 1; day <= last.getDate(); day += 1) {
        const date = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        cells.push(`<button class="day-cell" type="button" data-date="${date}">${day}<div class="day-meta">0/14</div></button>`);
    }
    calendar.innerHTML = cells.join('');
    calendar.querySelectorAll('[data-date]').forEach(cell => {
        cell.addEventListener('click', () => {
            if (!requireLogin()) return;
            state.selectedDate = cell.dataset.date;
            calendar.querySelectorAll('.day-cell').forEach(item => item.classList.remove('selected'));
            cell.classList.add('selected');
        });
    });
}

function renderReservationChoices() {
    const times = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00'];
    document.getElementById('timeGrid').innerHTML = times.map(time => `<button type="button" data-time="${time}">${time}</button>`).join('');
    document.getElementById('timeGrid').querySelectorAll('button').forEach(button => {
        button.addEventListener('click', () => {
            if (!requireLogin()) return;
            state.selectedTime = button.dataset.time;
            document.querySelectorAll('#timeGrid button').forEach(item => item.classList.remove('selected'));
            button.classList.add('selected');
        });
    });

    document.getElementById('courtList').innerHTML = [1, 2, 3, 4, 5]
        .map(num => `<button type="button" data-court="${num}번 코트">${num}번 코트 (예약 가능)</button>`)
        .join('');
    document.getElementById('courtList').querySelectorAll('button').forEach(button => {
        button.addEventListener('click', () => {
            if (!requireLogin()) return;
            state.selectedCourt = button.dataset.court;
            document.querySelectorAll('#courtList button').forEach(item => item.classList.remove('selected'));
            button.classList.add('selected');
        });
    });
}

function goReservationInfo() {
    if (!requireLogin()) return;
    if (!state.selectedDate || !state.selectedTime || !state.selectedCourt) {
        alert('날짜, 시간, 코트를 모두 선택해주세요.');
        return;
    }
    localStorage.setItem('bookkokReservationDraft', JSON.stringify({
        reservationDate: state.selectedDate,
        reservationTime: state.selectedTime,
        reservationCourt: state.selectedCourt
    }));
    location.href = '/reservations/info';
}

function renderReservationStep2() {
    if (!requireLogin()) return;
    const draft = JSON.parse(localStorage.getItem('bookkokReservationDraft') || '{}');
    setApp(html`
        ${renderSteps(2)}
        <section class="wide-panel">
            <h1 class="sub-title">예약 정보 입력</h1>
            <div class="notice">선택 정보: ${escapeHtml(draft.reservationDate)} / ${escapeHtml(draft.reservationTime)} / ${escapeHtml(draft.reservationCourt)}</div>
            <div class="form-row"><label for="reservationClubId">단체 ID</label><input class="field" id="reservationClubId" type="number" placeholder="예약할 단체 ID"></div>
            <div class="form-row"><label for="headcount">인원</label><input class="field" id="headcount" type="number" value="1"></div>
            <div class="form-row"><label for="applicantName">신청자명</label><input class="field" id="applicantName" value="${escapeHtml(state.memberId)}"></div>
            <div class="form-row"><label for="applicantPhone">휴대폰</label><input class="field" id="applicantPhone"></div>
            <div class="actions">
                <button class="secondary" type="button" onclick="location.href='/reservations'">이전</button>
                <button class="primary" id="reservationSubmit" type="button">예약 확정</button>
            </div>
        </section>
    `);
    document.getElementById('reservationSubmit').addEventListener('click', createReservation);
}

async function createReservation() {
    const draft = JSON.parse(localStorage.getItem('bookkokReservationDraft') || '{}');
    try {
        await api('/api/reservations', {
            method: 'POST',
            body: JSON.stringify({
                clubId: Number(value('reservationClubId')),
                reservationDate: draft.reservationDate,
                reservationCourt: draft.reservationCourt,
                reservationTime: draft.reservationTime,
                headcount: Number(value('headcount'))
            })
        });
        location.href = '/reservations/done';
    } catch (error) {
        alert(error.message || '예약에 실패했습니다. 단체 ID와 예약 정보를 확인해주세요.');
    }
}

function renderReservationDone() {
    setApp(html`
        ${renderSteps(3)}
        <section class="done">
            <h1>예약이 확정되었습니다</h1>
            <div class="check">✓</div>
            <button class="primary" type="button" onclick="location.href='/reservations'">예약 더 하기</button>
            <button class="secondary" type="button" onclick="location.href='/home'">홈으로</button>
        </section>
    `);
}
