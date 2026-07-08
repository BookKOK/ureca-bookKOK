async function renderBoard() {
    setApp(html`
        <section class="wide-panel">
            <div class="search-box">
                <div class="search-line">
                    <input class="field" id="postKeyword" placeholder="제목이나 내용으로 검색">
                    <button class="primary" id="postSearch" type="button">검색</button>
                </div>
            </div>
            <div class="actions">
                <button class="primary" type="button" onclick="location.href='/board/new'">글쓰기</button>
            </div>
            <table class="table">
                <thead>
                <tr>
                    <th style="width: 15%;">분류</th>
                    <th style="width: 50%;">제목</th>
                    <th style="width: 20%;">작성자</th>
                    <th style="width: 15%; text-align: center;">좋아요</th>
                </tr>
                </thead>
                <tbody id="postRows">
                <tr><td colspan="4" class="empty">불러오는 중...</td></tr>
                </tbody>
            </table>
        </section>
    `);
    document.getElementById('postSearch').addEventListener('click', () => {
        loadPosts(document.getElementById('postKeyword').value);
    });
    await loadPosts();
}

// keyword를 매개변수로 받고, 기본값은 빈 문자열로 설정합니다.
async function loadPosts(keyword = '') {
    const rows = document.getElementById('postRows');
    try {
        // 검색어가 있으면 검색 API 호출, 없으면 전체 목록 API 호출
        const url = keyword
            ? `/api/posts/search?keyword=${encodeURIComponent(keyword)}`
            : '/api/posts';

        const posts = await api(url);

        rows.innerHTML = posts.length
            ? posts.map(post => html`
                    <tr>
                        <td><span class="badge">${escapeHtml(post.categoryName || '일반')}</span></td>
                        <td><a class="post-title-link" href="/board/${post.postId}">${escapeHtml(post.title)}</a></td>
                        <td>${escapeHtml(post.authorName)}</td>
                        <td style="text-align: center;">❤️ ${post.likeCount ?? 0}</td>
                    </tr>
            `).join('')
            : '<tr><td colspan="4" class="empty">게시글이 없습니다.</td></tr>';
    } catch (error) {
        console.error("게시글 목록 로드 실패:", error);
        rows.innerHTML = '<tr><td colspan="4" class="empty">게시글 목록을 불러오지 못했습니다.</td></tr>';
    }
}

async function renderPostDetail(match) {
    const postId = match[1]; // 라우터 매칭에서 가져온 순수 게시글 번호
    setApp('<section class="wide-panel"><p class="empty">불러오는 중...</p></section>');

    // 안전장치: postId가 정상적으로 잡히지 않았다면 조기 차단
    if (!postId || postId === 'undefined') {
        console.error("오류: 게시글 ID를 라우터에서 읽지 못했습니다.", match);
        setApp('<section class="wide-panel"><p class="empty">올바르지 않은 접근입니다.</p></section>');
        return;
    }

    try {
        const post = await api(`/api/posts/${postId}`);
        setApp(html`
            <section class="wide-panel">
                <h1 class="sub-title">${escapeHtml(post.title)}</h1>
                <div class="detail-table">
                    <div class="detail-row">
                        <div class="detail-label">분류</div>
                        <div class="detail-value">${escapeHtml(post.categoryName || '일반')}</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">작성자</div>
                        <div class="detail-value">${escapeHtml(post.authorName)}</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">내용</div>
                        <div class="detail-value" style="white-space: pre-wrap;">${escapeHtml(post.content)}</div>
                    </div>
                </div>
                <div class="actions">
                    <button class="primary" id="likePost" type="button">❤️ 좋아요</button>
                    <button class="secondary" type="button" onclick="location.href='/board/${postId}/edit'">수정</button>
                    <button class="secondary" type="button" onclick="location.href='/board'">목록</button>
                </div>
                <div class="comment-box">
                    <h2 class="sub-title">댓글</h2>
                    <div id="commentList"></div>
                    <textarea id="commentContent" placeholder="댓글 내용"></textarea>
                    <div class="actions">
                        <button class="primary" id="commentSubmit" type="button">댓글 등록</button>
                    </div>
                </div>
            </section>
        `);

        // 💡 [핵심 보완] 매개변수 postId를 스코프에 확실히 묶어서 바인딩 처리
        const currentPostId = postId;

        document.getElementById('likePost').addEventListener('click', () => likePost(currentPostId));
        document.getElementById('commentSubmit').addEventListener('click', () => createComment(currentPostId));

        await loadComments(currentPostId);
    } catch (error) {
        console.error("게시글 상세 로드 실패:", error);
        setApp('<section class="wide-panel"><p class="empty">게시글을 불러오지 못했습니다.</p></section>');
    }
}

async function loadComments(postId) {
    const list = document.getElementById('commentList');
    try {
        const comments = await api(`/api/posts/${postId}/comments`);
        list.innerHTML = comments.length
            ? comments.map(comment => html`
                    <div class="comment-item">
                        <div class="comment-info">
                            <strong>${escapeHtml(comment.authorName)}</strong>
                        </div>
                        <p class="comment-content">${escapeHtml(comment.content)}</p>
                    </div>
            `).join('')
            : '<p class="empty">등록된 댓글이 없습니다.</p>';
    } catch (error) {
        console.error("댓글 로드 실패:", error);
        list.innerHTML = '<p class="empty">댓글을 불러오지 못했습니다.</p>';
    }
}

async function createComment(postId) {
    if (!requireLogin()) return;

    const commentContentTextarea = document.getElementById('commentContent');
    const contentValue = commentContentTextarea.value.trim();

    if (!contentValue) {
        alert('댓글 내용을 입력해주세요.');
        commentContentTextarea.focus();
        return;
    }

    try {
        // 💡 [핵심] 백엔드 DTO(CreateRequest)가 postId 필드를 원하고 있으므로,
        // 상자(Body) 안에 postId와 content를 둘 다 확실하게 채워서 보냅니다!
        await api(`/api/posts/${postId}/comments`, {
            method: 'POST',
            body: JSON.stringify({
                postId: Number(postId), // 👈 백엔드 DTO의 private Long postId;와 매핑됨!
                content: contentValue   // 👈 백엔드 DTO의 private String content;와 매핑됨!
            })
        });

        commentContentTextarea.value = '';
        await loadComments(postId); // 댓글 목록 새로고침
    } catch (error) {
        console.error("댓글 등록 실패:", error);
        alert('댓글 등록에 실패했습니다.');
    }
}

async function likePost(postId) {
    if (!requireLogin()) return;
    try {
        // 백엔드가 boolean(true/false)을 리턴하므로 결과를 받아서 동적으로 화면 처리 가능
        const isLiked = await api(`/api/posts/${postId}/like`, { method: 'POST' });
        alert(isLiked ? '좋아요가 반영되었습니다! ❤️' : '좋아요가 취소되었습니다! 🤍');

        // 새로고침 없이 수치만 살짝 바꿀 수 있도록 상세보기를 다시 불러오거나 reload
        location.reload();
    } catch (error) {
        console.error("좋아요 처리 실패:", error);
        alert('좋아요 처리에 실패했습니다.');
    }
}

async function renderPostForm(match) {
    if (!requireLogin()) return;
    const postId = match?.[1];
    let post = {};

    if (postId) {
        try {
            post = await api(`/api/posts/${postId}`);
        } catch (error) {
            console.error("수정용 기존 데이터 로드 실패:", error);
            alert('게시글 정보를 가져오지 못했습니다.');
            location.href = '/board';
            return;
        }
    }

    setApp(html`
        <section class="wide-panel">
            <h1 class="sub-title">${postId ? '게시글 수정' : '게시글 작성'}</h1>
            <div class="form-row">
                <label for="postCategoryId">카테고리 ID</label>
                <input class="field" id="postCategoryId" type="number" value="${post.categoryId || ''}" placeholder="카테고리 번호를 입력하세요">
            </div>
            <div class="form-row">
                <label for="postTitle">제목</label>
                <input class="field" id="postTitle" value="${escapeHtml(post.title || '')}" placeholder="제목을 입력하세요">
            </div>
            <div class="form-row">
                <label for="postContent">내용</label>
                <textarea id="postContent" placeholder="내용을 입력하세요" rows="10">${escapeHtml(post.content || '')}</textarea>
            </div>
            <div class="actions">
                <button class="secondary" type="button" onclick="location.href='${postId ? `/board/${postId}` : '/board'}'">취소</button>
                <button class="primary" id="postSubmit" type="button">저장</button>
            </div>
        </section>
    `);

    document.getElementById('postSubmit').addEventListener('click', () => savePost(postId));
}

async function savePost(postId) {
    const title = value('postTitle').trim();
    const content = value('postContent').trim();
    const categoryId = Number(value('postCategoryId'));

    if (!title || !content) {
        alert('제목과 내용을 모두 입력해주세요.');
        return;
    }

    try {
        const payload = {
            categoryId: categoryId || null,
            title: title,
            content: content
        };

        if (postId) {
            // [수정 모드] 백엔드가 이제 수정된 Response 객체를 리턴합니다.
            const result = await api(`/api/posts/${postId}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });
            // 백엔드가 돌려준 결과(result) 안의 postId를 쓰거나 기존의 postId를 사용해 안전하게 이동
            location.href = `/board/${postId || result.postId}`;
        } else {
            // [등록 모드]
            const newPostId = await api('/api/posts', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            location.href = `/board/${newPostId}`;
        }
    } catch (error) {
        console.error("게시글 저장 실패:", error);
        alert('게시글 저장에 실패했습니다. 내용을 다시 확인해 주세요.');
    }
}