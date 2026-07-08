// 1. 내(우리 단체) 예약 목록 화면 렌더링
async function renderMyReservations() {
    if (!requireLogin()) return;

    setApp(html`
        <section class="wide-panel">
            <h1 class="page-title">나의 예약 목록</h1>
            <div id="reservationRows" class="list-container">
                <div style="padding: 2rem; text-align: center; color: #888;">불러오는 중...</div>
            </div>
        </section>
    `);

    await loadMyReservations();
}

// 예약 목록 데이터 불러오기
async function loadMyReservations() {
    const rowsEl = document.getElementById('reservationRows');

    try {
        //1. 현재 로그인한 사용자의 소속 단체 ID(clubId) 가져오기
        const userInfo = await api('/api/reservations/info');

        if (!userInfo || !userInfo.clubId) {
            rowsEl.innerHTML = '<div style="padding: 2rem; text-align: center; color: #888;">소속된 단체가 없습니다. 단체 가입 후 이용해주세요.</div>';
            return;
        }

        //2. 우리 단체의 예약 목록 조회하기 (단체원 전체에게 공유됨)
        const reservations = await api(`/api/reservations/my?clubId=${userInfo.clubId}`);

        if (!reservations || reservations.length === 0) {
            rowsEl.innerHTML = '<div style="padding: 2rem; text-align: center; color: #888;">예약 내역이 없습니다.</div>';
            return;
        }

        //3-1. 날짜가 지나지 않은 예약 필터링
        const now = new Date();
        const validReservations = reservations.filter(res => {
            const timeStr = res.reservationTime && res.reservationTime.length === 5
                            ? res.reservationTime + ":00"
                            : res.reservationTime || "00:00:00";

            const reservationDateTime = new Date(`${res.reservationDate}T${timeStr}`);
            return reservationDateTime >= now;
        })

        if (validReservations.length === 0) {
            rowsEl.innerHTML = '<div style="padding: 2rem; text-align: center; color: #888;">예약 내역이 없습니다.</div>';
            return;
        }

        //3-2. 필터링된 예약들을 날짜 기준 오름차순으로 정렬
        const sortedReservations = validReservations.sort((a, b) =>
                                        new Date(a.reservationDate) - new Date(b.reservationDate));

        //4. 예약 목록 화면에 그리기 (클릭 시 상세 페이지로 이동)
        rowsEl.innerHTML = sortedReservations.map((res, index) => html`
            <div class="list-row">
                <div>
                    <span class="list-num">${index + 1}</span>
                    <span class="list-link" onclick="location.href='/reservations/detail/${res.reservationId}'">
                        ${escapeHtml(res.reservationCourt)}
                    </span>
                </div>
                
                <div class="list-info">
                    <span><strong>날짜: </strong>${escapeHtml(res.reservationDate)}</span>
                    <span><strong>시간: </strong>${String(res.reservationTime || '').substring(0, 5)}</span>
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error("예약 목록 조회 실패:", error);
        rowsEl.innerHTML = '<div style="padding: 2rem; text-align: center; color: #e53e3e;">예약 목록을 불러오지 못했습니다.</div>';
    }
}

// 2. 예약 단건 상세 조회 화면 렌더링
async function renderReservationDetail(match) {
    if (!requireLogin()) return;

    // URL에서 reservationId 추출 (예: /reservations/detail/3 -> 3)
    const reservationId = match[1];
    setApp('<div style="padding: 2rem; text-align: center;">불러오는 중...</div>');

    try {
        const res = await api(`/api/reservations/${reservationId}`);

        setApp(html`
            <section class="wide-panel">
                <h1 class="page-title">예약 상세 내역</h1>
                
                <div class="detail-table">
                    <div class="detail-row">
                        <div class="detail-label">예약 단체</div>
                        <div class="detail-value" style="font-weight: bold;">${escapeHtml(res.clubName)}</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">예약 일자</div>
                        <div class="detail-value">${escapeHtml(res.reservationDate)}</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">예약 시간</div>
                        <div class="detail-value">${String(res.reservationTime || '').substring(0, 5)}</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">이용 코트</div>
                        <div class="detail-value">${escapeHtml(res.reservationCourt)}</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">참여 인원</div>
                        <div class="detail-value">${res.headcount}명</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">예약 신청일</div>
                        <div class="detail-value">${escapeHtml(res.createdDate || '')}</div>
                    </div>
                </div>

                <div class="actions" style="display: flex; gap: 10px; justify-content: space-between;">
                    <button class="secondary" type="button" onclick="location.href='/reservations/my'">목록으로</button>
                    <button class="danger" type="button" id="cancelResBtn" style="background-color: #e53e3e; color: white;">예약 취소</button>
                </div>
            </section>
        `);

        // 예약 취소 버튼 이벤트 연결
        document.getElementById('cancelResBtn').addEventListener('click', () => {
            cancelReservation(reservationId);
        });

    } catch (error) {
        alert(error.message || '예약 정보를 불러올 수 없습니다.');
        location.href = '/reservations/my';
    }
}


// 3. 예약 취소
async function cancelReservation(reservationId) {
    if (!confirm('정말로 이 예약을 취소하시겠습니까?')) return;

    try {
        await api(`/api/reservations/${reservationId}`, {
            method: 'DELETE'
        });
        alert('예약이 정상적으로 취소되었습니다.');
        location.href = '/reservations/my';
    } catch (error) {
        alert(error.message || '예약 취소에 실패했습니다.');
    }
}