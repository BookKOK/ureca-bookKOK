/*
 * router.js
 *
 * index.html에서 가장 마지막에 로드되는 파일.
 *
 * 이유:
 * - app.js가 공통 함수(setApp, api, applyAuthUi 등)를 먼저 준비
 * - 화면별 JS가 renderLogin, renderBoard 같은 render 함수 먼저 등록
 * - 그 다음 router.js가 현재 URL을 보고 알맞은 render 함수 호출
 */

// 현재 브라우저 URL과 실행할 화면 render 함수를 연결한 라우팅 테이블.
//
// 예:
// - /login      -> auth.js의 renderLogin()
// - /board      -> board.js의 renderBoard()
// - /board/3    -> board.js의 renderPostDetail(match)
// - /admin      -> admin.js의 renderAdmin()
const routes = [
    { pattern: /^\/ui$|^\/login$/, render: renderLogin },
    { pattern: /^\/signup$/, render: renderSignup },
    { pattern: /^\/$|^\/home$|^\/reservations$/, render: renderReservationStep1 },
    { pattern: /^\/reservations\/info$/, render: renderReservationStep2 },
    { pattern: /^\/reservations\/done$/, render: renderReservationDone },
    { pattern: /^\/clubs$/, render: renderClubs },
    { pattern: /^\/clubs\/new$/, render: renderClubForm },
    { pattern: /^\/clubs\/(\d+)$/, render: renderClubDetail },
    { pattern: /^\/board$/, render: renderBoard },
    { pattern: /^\/board\/new$/, render: renderPostForm },
    { pattern: /^\/board\/(\d+)$/, render: renderPostDetail },
    { pattern: /^\/board\/(\d+)\/edit$/, render: renderPostForm },
    { pattern: /^\/mypage$/, render: renderMyPage },
	{ pattern: /^\/mypage\/password$/, render: renderPasswordPage },
    { pattern: /^\/admin$/, render: renderAdmin }
];

// index.html과 모든 JS 파일이 로드된 뒤 마지막에 실행되는 진입 함수.
//
// 실행 흐름:
// 1. index.html이 공통 레이아웃 생성
// 2. app.js가 공통 상태와 함수 준비
// 3. 화면별 JS 파일들이 render 함수 등록
// 4. boot() 함수가 현재 URL(location.pathname) 확인
// 5. routes에서 URL 패턴과 맞는 항목 찾기
// 6. 찾은 render 함수를 실행해 index.html의 #app 영역에 화면 그리기
function boot() {
    // 이전 화면에서 열린 모달이 남아 있지 않도록 먼저 닫기.
    hideModal();

    // localStorage의 JWT/회원/권한 정보를 state에 다시 동기화.
    refreshAuthFromStorage();

    // 로그인 여부와 관리자 여부에 따라 상단 버튼/관리자 메뉴 갱신.
    applyAuthUi();

    // 현재 URL 경로 확인. 예: /login, /board, /reservations
    const path = location.pathname;

    // routes 배열에서 현재 URL과 매칭되는 라우트 찾기.
    const match = routes.find(route => route.pattern.test(path));

    // 등록되지 않은 URL이면 기본적으로 홈 화면 표시.
    if (!match) {
        renderHome();
        return;
    }

    // 매칭된 화면 render 함수 실행.
    // 동적 URL은 match 결과를 넘겨 상세 ID를 사용할 수 있게 처리.
    // 예: /board/3 -> renderPostDetail(match), match[1] === "3"
    match.render(path.match(match.pattern));
}

// router.js가 로드되면 즉시 boot() 실행.
// 현재 URL에 맞는 화면 렌더링 시작점.
boot();
