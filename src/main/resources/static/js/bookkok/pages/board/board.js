async function renderBoard() {
    setApp(html`
        <section class="wide-panel">
            <div class="board-filter"></div>
            <div class="actions">
                <button class="primary" type="button" onclick="location.href='/board/new'">글쓰기</button>
            </div>
            <table class="table">
                <thead><tr><th>분류</th><th>제목</th><th>작성자</th><th>좋아요</th></tr></thead>
                <tbody id="postRows"><tr><td colspan="4" class="empty">불러오는 중...</td></tr></tbody>
            </table>
        </section>
    `);
    await loadPosts();
}

async function loadPosts() {
    const rows = document.getElementById('postRows');
    try {
        const posts = await api('/api/posts');
        rows.innerHTML = posts.length ? posts.map(post => html`
            <tr>
                <td>${escapeHtml(post.categoryName || '일반')}</td>
                <td><a class="post-title-link" href="/board/${post.postId}">${escapeHtml(post.title)}</a></td>
                <td>${escapeHtml(post.authorName)}</td>
                <td style="text-align:center">${post.likeCount ?? 0}</td>
            </tr>
        `).join('') : '<tr><td colspan="4" class="empty">게시글이 없습니다.</td></tr>';
    } catch (error) {
        rows.innerHTML = '<tr><td colspan="4" class="empty">게시글 목록을 불러오지 못했습니다.</td></tr>';
    }
}

async function renderPostDetail(match) {
    const postId = match[1];
    setApp('<section class="wide-panel"><p class="empty">불러오는 중...</p></section>');
    try {
        const post = await api(`/api/posts/${postId}`);
        setApp(html`
            <section class="wide-panel">
                <h1 class="sub-title">${escapeHtml(post.title)}</h1>
                <div class="detail-table">
                    <div class="detail-row"><div class="detail-label">분류</div><div class="detail-value">${escapeHtml(post.categoryName)}</div></div>
                    <div class="detail-row"><div class="detail-label">작성자</div><div class="detail-value">${escapeHtml(post.authorName)}</div></div>
                    <div class="detail-row"><div class="detail-label">내용</div><div class="detail-value">${escapeHtml(post.content)}</div></div>
                </div>
                <div class="actions">
                    <button class="primary" id="likePost" type="button">좋아요</button>
                    <button class="secondary" type="button" onclick="location.href='/board/${postId}/edit'">수정</button>
                    <button class="secondary" type="button" onclick="location.href='/board'">목록</button>
                </div>
                <div class="comment-box">
                    <h2 class="sub-title">댓글</h2>
                    <div id="commentList"></div>
                    <textarea id="commentContent" placeholder="댓글 내용"></textarea>
                    <div class="actions"><button class="primary" id="commentSubmit" type="button">댓글 등록</button></div>
                </div>
            </section>
        `);
        document.getElementById('likePost').addEventListener('click', () => likePost(postId));
        document.getElementById('commentSubmit').addEventListener('click', () => createComment(postId));
        await loadComments(postId);
    } catch (error) {
        setApp('<section class="wide-panel"><p class="empty">게시글을 불러오지 못했습니다.</p></section>');
    }
}

async function loadComments(postId) {
    const list = document.getElementById('commentList');
    try {
        const comments = await api(`/api/posts/${postId}/comments`);
        list.innerHTML = comments.length ? comments.map(comment => html`
            <div class="comment-item"><strong>${escapeHtml(comment.authorName)}</strong><p>${escapeHtml(comment.content)}</p></div>
        `).join('') : '<p class="empty">댓글이 없습니다.</p>';
    } catch (error) {
        list.innerHTML = '<p class="empty">댓글을 불러오지 못했습니다.</p>';
    }
}

async function createComment(postId) {
    if (!requireLogin()) return;
    try {
        await api(`/api/posts/${postId}/comments`, {
            method: 'POST',
            body: JSON.stringify({ postId: Number(postId), content: value('commentContent') })
        });
        await loadComments(postId);
        document.getElementById('commentContent').value = '';
    } catch (error) {
        alert('댓글 등록에 실패했습니다.');
    }
}

async function likePost(postId) {
    if (!requireLogin()) return;
    try {
        await api(`/api/posts/${postId}/like`, { method: 'POST' });
        alert('처리되었습니다.');
    } catch (error) {
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
            post = {};
        }
    }
    setApp(html`
        <section class="wide-panel">
            <h1 class="sub-title">${postId ? '게시글 수정' : '게시글 작성'}</h1>
            <div class="form-row"><label>카테고리 ID</label><input class="field" id="postCategoryId" type="number"></div>
            <div class="form-row"><label>제목</label><input class="field" id="postTitle" value="${escapeHtml(post.title || '')}"></div>
            <div class="form-row"><label>내용</label><textarea id="postContent">${escapeHtml(post.content || '')}</textarea></div>
            <div class="actions">
                <button class="secondary" type="button" onclick="location.href='/board'">취소</button>
                <button class="primary" id="postSubmit" type="button">저장</button>
            </div>
        </section>
    `);
    document.getElementById('postSubmit').addEventListener('click', () => savePost(postId));
}

async function savePost(postId) {
    try {
        const payload = {
            categoryId: Number(value('postCategoryId')),
            title: value('postTitle'),
            content: value('postContent')
        };
        const result = await api(postId ? `/api/posts/${postId}` : '/api/posts', {
            method: postId ? 'PUT' : 'POST',
            body: JSON.stringify(payload)
        });
        location.href = `/board/${postId || result}`;
    } catch (error) {
        alert('게시글 저장에 실패했습니다. 카테고리 ID를 확인해주세요.');
    }
}
