<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 - 회원 관리</title>
    <%@ include file="/component/head.jsp" %>
    <style>
        /* Small local tweaks to complement global styles */
        .card { background: white; border-radius: 12px; padding: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
        .badge-yes { background:#E6FFFA; color:#048A81; padding:4px 8px; border-radius:999px; font-weight:600; }
        .badge-no { background:#FFF5F5; color:#C53030; padding:4px 8px; border-radius:999px; font-weight:600; }
        .table-scroll { overflow-x:auto; }
    </style>
</head>
<body class="min-h-screen bg-gray-50 text-slate-900">
<%@ include file="/component/header.jsp" %>

<div class="max-w-6xl mx-auto px-4 py-10">
    <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-semibold">회원 관리</h1>
        <div class="flex items-center gap-3">
            <label for="searchInput" class="sr-only">검색</label>
            <input id="searchInput" type="search" placeholder="닉네임 또는 이메일로 검색" class="px-4 py-2 rounded-lg border bg-white" />
            <button id="searchBtn" class="px-4 py-2 bg-[#FF2C00] text-white rounded-lg">검색</button>
        </div>
    </div>

    <div class="card mb-6">
        <div class="flex items-center justify-between mb-4">
            <div>
                <div class="text-sm text-gray-500">총 회원</div>
                <div class="text-xl font-bold">${fn:length(members)}</div>
            </div>
            <div class="text-sm text-gray-500">관리 작업: 회원 정보 수정 및 삭제</div>
        </div>

        <div class="table-scroll">
            <table class="w-full text-left">
                <thead>
                <tr class="text-sm text-gray-600 border-b">
                    <th class="py-3">회원</th>
                    <th class="py-3">이메일</th>
                    <th class="py-3">소개</th>
                    <th class="py-3">관리자</th>
                    <th class="py-3">활동</th>
                </tr>
                </thead>
                <tbody id="membersTbody">
                <c:forEach var="m" items="${members}">
                    <tr class="border-b hover:bg-gray-50" data-member-id="${m.id}" data-member-nickname="${fn:escapeXml(m.nickname)}" data-member-avatar="${fn:escapeXml(m.avatar)}" data-member-intro="${fn:escapeXml(m.introduce)}">
                        <td class="py-4 align-middle">
                            <div class="flex items-center gap-3">
                                <img src="${m.avatar}" alt="avatar" class="w-10 h-10 rounded-full object-cover" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/recipe-hub.svg'" />
                                <div>
                                    <div class="font-medium">${fn:escapeXml(m.nickname)}</div>
                                    <div class="text-sm text-gray-500">ID: ${m.id}</div>
                                </div>
                            </div>
                        </td>
                        <td class="py-4 text-sm text-gray-600">${fn:escapeXml(m.email)}</td>
                        <td class="py-4 text-sm text-gray-700 truncate max-w-[28rem]">${fn:escapeXml(m.introduce)}</td>
                        <td class="py-4">
                            <c:choose>
                                <c:when test="${m.admin}">
                                    <span class="badge-yes">관리자</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge-no">사용자</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="py-4">
                            <div class="flex items-center gap-2">
                                <button class="px-3 py-1 rounded-md bg-blue-600 text-white" onclick="openEditModalFromRow(this)">수정</button>
                                <button class="px-3 py-1 rounded-md bg-red-600 text-white" onclick="deleteMemberFromRow(this)">삭제</button>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Edit Modal -->
    <div id="editModal" class="fixed inset-0 z-50 hidden items-center justify-center bg-black/40">
        <div class="bg-white rounded-lg w-full max-w-lg p-6">
            <div class="flex items-start justify-between">
                <h3 class="text-lg font-semibold">회원 정보 수정</h3>
                <button onclick="closeEditModal()" class="text-gray-500">닫기</button>
            </div>
            <form id="editForm" class="mt-4">
                <input type="hidden" id="editMemberId" name="memberId">
                <div class="grid grid-cols-1 gap-3">
                    <label class="text-sm">닉네임
                        <input id="editNickname" name="nickname" type="text" class="w-full mt-1 px-3 py-2 border rounded-md" />
                    </label>

                    <label class="text-sm">아바타 URL
                        <input id="editAvatar" name="avatar" type="text" class="w-full mt-1 px-3 py-2 border rounded-md" />
                    </label>

                    <label class="text-sm">소개
                        <textarea id="editIntroduction" name="introduction" rows="4" class="w-full mt-1 px-3 py-2 border rounded-md"></textarea>
                    </label>

                    <div class="flex justify-end gap-2 mt-2">
                        <button type="button" onclick="closeEditModal()" class="px-4 py-2 rounded-md">취소</button>
                        <button type="submit" class="px-4 py-2 bg-[#FF2C00] text-white rounded-md">저장</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

</div>

<script>
    const adminMemberUrl = '${pageContext.request.contextPath}/admin/member';

    // Search functionality (client-side simple filter)
    document.getElementById('searchBtn').addEventListener('click', function(){
        const q = document.getElementById('searchInput').value.toLowerCase().trim();
        const rows = document.querySelectorAll('#membersTbody tr');
        rows.forEach(r => {
            const nick = (r.dataset.memberNickname || '').toLowerCase();
            const emailCell = r.querySelector('td:nth-child(2)')?.textContent || '';
            const email = emailCell.toLowerCase();
            if(!q || nick.includes(q) || email.includes(q)) r.style.display = '';
            else r.style.display = 'none';
        });
    });

    // Open modal by row button
    function openEditModalFromRow(btn){
        const tr = btn.closest('tr');
        openEditModalWithData(tr.dataset);
    }

    // Delete by row
    function deleteMemberFromRow(btn){
        const tr = btn.closest('tr');
        const id = tr.dataset.memberId;
        deleteMember(id);
    }

    function openEditModalWithData(data){
        document.getElementById('editMemberId').value = data.memberId || '';
        document.getElementById('editNickname').value = data.memberNickname || '';
        document.getElementById('editAvatar').value = data.memberAvatar || '';
        document.getElementById('editIntroduction').value = data.memberIntro || '';
        document.getElementById('editModal').classList.remove('hidden');
        document.getElementById('editModal').classList.add('flex');
    }

    function closeEditModal(){
        document.getElementById('editModal').classList.add('hidden');
        document.getElementById('editModal').classList.remove('flex');
    }

    // submit edit (PUT)
    document.getElementById('editForm').addEventListener('submit', async function(e){
        e.preventDefault();
        const form = new URLSearchParams();
        form.append('memberId', document.getElementById('editMemberId').value);
        form.append('nickname', document.getElementById('editNickname').value);
        form.append('avatar', document.getElementById('editAvatar').value);
        form.append('introduction', document.getElementById('editIntroduction').value);

        try{
            const res = await fetch(adminMemberUrl, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                credentials: 'include',
                body: form
            });
            const json = await res.json();
            if(!res.ok){ alert('수정 실패: '+(json.error||'오류')); return; }
            alert(json.message);
            location.reload();
        }catch(err){ console.error(err); alert('서버 오류'); }
    });

    // delete member
    async function deleteMember(id){
        if(!confirm('정말 삭제하시겠습니까?')) return;
        const form = new URLSearchParams(); form.append('memberId', id);
        try{
            const res = await fetch(adminMemberUrl, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                credentials: 'include',
                body: form
            });
            const json = await res.json();
            if(!res.ok){ alert('삭제 실패: '+(json.error||'오류')); return; }
            alert(json.message);
            location.reload();
        }catch(err){ console.error(err); alert('서버 오류'); }
    }
</script>

</body>
</html>
