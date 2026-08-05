async function renderBoard() {
    setApp(html`
        <section class="wide-panel">
            <div class="search-box" style="display: flex; justify-content: flex-end; align-items: center; gap: 8px;">

                <select id="searchType" style="width: 130px; padding: 0 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px;
                 background: white; cursor: pointer; height: 35px; box-sizing: border-box; font-family: inherit; color: #333;">
                    <option value="title">제목만</option>
                    <option value="title_content" selected>제목 + 내용</option>
                    <option value="content">내용만</option>
                </select>

                <div class="search-line">
                    <input class="field" id="postKeyword" placeholder="제목이나 내용으로 검색">
                    <button class="primary" id="postSearch" type="button" onclick="searchPosts()">검색</button>
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

    // 💡 원래 있던 검색 버튼을 찾아서 클릭 이벤트를 강제로 안전하게 먹여줍니다!
    const searchBtn = document.getElementById('postSearch');
    if (searchBtn) {
        searchBtn.addEventListener('click', searchPosts);
    }
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
    const postId = match[1];

    if (!postId || postId === 'undefined') {
        setApp('<section class="wide-panel"><p class="empty">올바르지 않은 접근입니다.</p></section>');
        return;
    }

    setApp('<section class="wide-panel"><p class="empty">불러오는 중...</p></section>');

    try {
        // [개선] API 중복 호출 제거, 병렬 처리
        const [post, isLiked] = await Promise.all([
            api(`/api/posts/${postId}`),
            api(`/api/posts/${postId}/like-status`)
        ]);

        // [핵심] isLiked 상태에 따른 하트 아이콘 결정
        const heartIcon = isLiked ? '❤️' : '🤍';

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
                <div class="actions" style="display: flex; gap: 8px; align-items: center; margin-top: 15px;">
                    <button id="likePost" class="primary" type="button" style="display: flex; align-items: center; gap: 6px;">
                        <span id="likeIcon">${heartIcon}</span>
                        <span>좋아요</span>
                    </button>

                    <button class="secondary" type="button" onclick="location.href='/board/${postId}/edit'"
                            style="padding: 8px 18px;">수정</button>

                    <button class="secondary" type="button" onclick="location.href='/board'"
                            style="padding: 8px 18px;">목록</button>
                </div>
                <div class="comment-box">
                    <h2 class="sub-title">댓글</h2>
                    <div id="commentList"></div>
                    <textarea id="commentContent" placeholder="댓글을 입력하세요"
                              style="width: 100%; min-height: 80px; padding: 12px; border: 1px solid #ccc; border-radius: 4px; 
                              resize: none; text-align: left; line-height: 1.5; font-family: inherit; box-sizing: border-box;"></textarea>
                    <div class="actions">
                        <button class="primary" id="commentSubmit" type="button">댓글 등록</button>
                    </div>
                </div>
            </section>
        `);

        // 이벤트 리스너 등록
        document.getElementById('likePost').addEventListener('click', () => likePost(postId));
        document.getElementById('commentSubmit').addEventListener('click', () => createComment(postId));

        await loadComments(postId);
    } catch (error) {
        console.error("게시글 상세 로드 실패:", error);
        setApp('<section class="wide-panel"><p class="empty">게시글을 불러오지 못했습니다.</p></section>');
    }
}

async function loadComments(postId) {
    const list = document.getElementById('commentList');
    const realCurrentUserId = localStorage.getItem('bookkokMemberId');

    try {
        const comments = await api(`/api/posts/${postId}/comments`);

        list.innerHTML = comments.length ? comments.map(comment => {
            const isMyComment = comment.authorId && realCurrentUserId && (String(comment.authorId) === String(realCurrentUserId));
            const rawDate = comment.createdDate;
            const displayDate = rawDate ? new Date(rawDate).toLocaleString() : '방금 전';

            return html`
                <div class="comment-item" id="commentItem-${comment.commentId}" style="position: relative; padding: 12px; border-bottom: 1px solid #eee;
                 display: flex; flex-direction: column; gap: 4px;">
                    <div class="comment-header" style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 14px; color: #333;">${escapeHtml(comment.authorName)}</strong>
                        
                        ${isMyComment ? html`
                            <div class="comment-menu-container" style="position: relative; display: inline-block;">
                                <button class="comment-menu-btn" onclick="toggleCommentMenu(${comment.commentId}); event.stopPropagation();" 
                                        style="background: none; border: none; font-size: 18px; cursor: pointer; color: #777; padding: 0 8px; font-weight: bold;">⋮</button>
                                
                                <div id="commentMenu-${comment.commentId}" class="comment-dropdown" 
                                     style="display: none; position: absolute; right: 0; top: 28px; background: white; border: 1px solid #ddd; border-radius: 6px;
                                      box-shadow: 0 4px 10px rgba(0,0,0,0.15); z-index: 999; min-width: 75px; text-align: center; overflow: hidden;">
                                    
                                    <button onclick="enableCommentEdit(${comment.commentId}, '${escapeHtml(comment.content)}', ${postId}); event.stopPropagation();" 
                                            style="display: block; width: 100%; padding: 8px 0; border: none; background: none; font-size: 13px; cursor: pointer; 
                                            color: #333; text-align: center; transition: background 0.2s;" onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">수정</button>
                                    
                                    <button onclick="deleteComment(${postId}, ${comment.commentId}); event.stopPropagation();" class="delete-btn" 
                                            style="display: block; width: 100%; padding: 8px 0; border: none; background: none; font-size: 13px; cursor: pointer; color: #ff4d4f; text-align: center; border-top: 1px solid #eee; transition: background 0.2s;"
                                            onmouseover="this.style.backgroundColor='#fff1f0'" onmouseout="this.style.backgroundColor='transparent'">삭제</button>
                                </div>
                            </div>
                        ` : ''}
                    </div>
                    
                    <div id="commentBodyContainer-${comment.commentId}">
                        <p class="comment-content" style="margin: 4px 0; font-size: 14px; color: #444; white-space: pre-wrap;">${escapeHtml(comment.content)}</p>
                    </div>

                    <small style="color: #999; font-size: 12px;">
                        ${displayDate} ${comment.isModified ? '(수정됨)' : ''}
                    </small>
                </div>
            `;
        }).join('') : '<div class="no-comment" style="padding: 20px; text-align: center; color: #999;">댓글이 없습니다.</div>';
    } catch (error) {
        list.innerHTML = '<div style="padding: 20px; text-align: center; color: red;">댓글을 불러오지 못했습니다.</div>';
    }
}

// 💡 3. 그 자리에서 즉시 수정 창을 열어주는 함수 (인라인 전환)
function enableCommentEdit(commentId, currentContent, postId) {
    const container = document.getElementById(`commentBodyContainer-${commentId}`);
    if (!container) return;

    // 대댓글 폼이나 중복 열기를 방어하고 깔끔하게 텍스트 박스로 체인지
    container.innerHTML = `
        <div style="margin: 8px 0; display: flex; flex-direction: column; gap: 6px;">
            <textarea id="editInput-${commentId}" style="width: 100%; min-height: 60px; padding: 8px; border: 1px solid #ccc; border-radius: 4px;
            font-size: 14px; resize: none; font-family: inherit;">${currentContent}</textarea>
            <div style="display: flex; justify-content: flex-end; gap: 6px;">
                <button onclick="cancelCommentEdit(${commentId}, '${escapeHtml(currentContent)}', ${postId})" style="padding: 4px 10px;
                 background: #eee; border: 1px solid #ccc; border-radius: 4px; font-size: 12px; cursor: pointer;">취소</button>
                <button onclick="submitCommentEdit(${commentId}, ${postId})" style="padding: 4px 10px; background: #049b7d; color: white;
                 border: none; border-radius: 4px; font-size: 12px; cursor: pointer; font-weight: bold;">저장</button>
            </div>
        </div>
    `;
}

// 💡 4. 인라인 수정 취소 시 원래 글 내용으로 원복하는 함수
function cancelCommentEdit(commentId, originalContent, postId) {
    const container = document.getElementById(`commentBodyContainer-${commentId}`);
    if (container) {
        container.innerHTML = `<p class="comment-content" style="margin: 4px 0; font-size: 14px; color: #444; white-space: pre-wrap;">${escapeHtml(originalContent)}</p>`;
    }
    // 수정창 닫힐 때 더보기 메뉴 전체 닫기
    closeAllCommentMenus();
}

// 💡 5. 인라인 수정본 최종 저장 및 PUT 전송 (백엔드 content 규격 완전 연동)
async function submitCommentEdit(commentId, postId) {
    const editInput = document.getElementById(`editInput-${commentId}`);
    if (!editInput) return;

    const newContent = editInput.value.trim();
    if (!newContent) {
        alert('내용을 입력해 주세요.');
        return;
    }

    try {
        // 백엔드 UpdateRequest DTO 규격 주소와 content 변수명 일치
        await api(`/api/comments/${commentId}`, {
            method: 'PUT',
            body: JSON.stringify({ content: newContent })
        });

        alert('댓글이 수정되었습니다.');
        await loadComments(postId); // 수려하게 댓글 창 목록만 비동기 갱신!
    } catch (error) {
        alert('댓글 수정 실패: ' + error.message);
    }
}

// 유틸리티 토글 함수들
function toggleCommentMenu(commentId) {
    const currentMenu = document.getElementById(`commentMenu-${commentId}`);
    const isHidden = currentMenu && currentMenu.style.display === 'none';
    closeAllCommentMenus();
    if (currentMenu && isHidden) {
        currentMenu.style.display = 'block';
    }
}

function closeAllCommentMenus() {
    document.querySelectorAll('.comment-dropdown').forEach(menu => {
        menu.style.display = 'none';
    });
}

// 화면 다른 곳 클릭 시 메뉴 상자 자동 닫기
document.addEventListener('click', () => closeAllCommentMenus());
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
        // 서버 요청
        const isLiked = await api(`/api/posts/${postId}/like`, { method: 'POST' });

        // 하트 아이콘 요소만 찾기
        const likeIcon = document.getElementById('likeIcon');

        // 아이콘 즉시 변경
        if (likeIcon) {
            likeIcon.innerText = isLiked ? '❤️' : '🤍';
        }
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
                <textarea id="postContent" placeholder="내용을 입력하세요"style="width: 100%; min-height: 200px;
                 padding: 12px; border: 1px solid #ccc; border-radius: 4px; resize: none; box-sizing: border-box;">${escapeHtml(post.content || '')}</textarea>
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

async function deleteComment(postId, commentId) { // 렌더러에서 postId를 넘겨주므로 파라미터 유지
    if (!confirm('댓글을 정말 삭제하시겠습니까?')) return;

    try {
        // 💡 중요: 컨트롤러에서 바꾼 주소인 /api/comments/{id} 규격에 정확히 맞춤!
        await api(`/api/comments/${commentId}`, {
            method: 'DELETE'
        });

        alert('댓글이 삭제되었습니다.');
        await loadComments(postId); // 삭제 완료 후 댓글창 새로고침
    } catch (error) {
        alert('댓글 삭제 실패: ' + error.message);
    }
}

// 🛠️ 2. 댓글 수정 액션 (자바 DTO content 매핑 - 500 에러 완벽 해결)
async function editComment(commentId, oldContent, postId) {
    // 💡 팁: 수정 후 댓글창 새로고침을 위해 렌더링 영역(from board.js)에서 postId를 인자로 추가로 받아오면 좋습니다.
    const newContent = prompt('수정할 내용을 입력하세요:', oldContent);
    if (!newContent || newContent.trim() === '' || newContent.trim() === oldContent) return;

    try {
        // 💡 주소는 백엔드 규칙에 맞추고, 바디의 키값을 자바 DTO 변수명인 'content'와 1:1 싱크로 일치시킴!
        await api(`/api/comments/${commentId}`, {
            method: 'PUT',
            body: JSON.stringify({
                content: newContent.trim()
            })
        });

        alert('댓글이 수정되었습니다.');

        // 만약 postId를 인자로 받았다면 부드럽게 댓글 목록만 새로고침 처리!
        if (postId) {
            await loadComments(postId);
        } else {
            location.reload();
        }
    } catch (error) {
        alert('댓글 수정 실패: ' + error.message);
    }
}

async function searchPosts() {
    const type = document.getElementById('searchType').value;
    const keyword = document.getElementById('postKeyword').value.trim(); // 원래 사용하던 ID 'postKeyword'
    const rows = document.getElementById('postRows');

    if (!keyword) {
        await loadPosts(); // 검색어 없으면 전체 목록 복원
        return;
    }

    try {
        // 백엔드 주소 규격에 필터(type)와 검색어(keyword)를 실어서 요청 전송
        const posts = await api(`/api/posts/search?type=${type}&keyword=${encodeURIComponent(keyword)}`);

        rows.innerHTML = posts.length ? posts.map(post => html`
            <tr>
                <td>${escapeHtml(post.categoryName || '일반')}</td>
                <td style="cursor:pointer;" onclick="renderPostDetail([null, ${post.postId}])">${escapeHtml(post.title)}</td>
                <td>${escapeHtml(post.authorName)}</td>
                <td>${post.likeCount ?? 0}</td>
            </tr>
        `).join('') : '<tr><td colspan="4" style="text-align:center; padding: 25px; color:#999;">검색 결과가 없습니다.</td></tr>';
    } catch (error) {
        rows.innerHTML = '<tr><td colspan="4" style="color:red; text-align:center; padding: 25px;">검색에 실패했습니다. 다시 시도해 주세요.</td></tr>';
    }
}