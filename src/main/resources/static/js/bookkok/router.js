const routes = [
    { pattern: /^\/$|^\/ui$|^\/login$/, render: renderLogin },
    { pattern: /^\/signup$/, render: renderSignup },
    { pattern: /^\/home$/, render: renderHome },
    { pattern: /^\/reservations$/, render: renderReservationStep1 },
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
    { pattern: /^\/admin$/, render: renderAdmin }
];


function boot() {
    hideModal();
    refreshAuthFromStorage();
    applyAuthUi();
    const path = location.pathname;
    const match = routes.find(route => route.pattern.test(path));
    if (!match) {
        renderHome();
        return;
    }
    match.render(path.match(match.pattern));
}

boot();

