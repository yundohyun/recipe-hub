<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 - 공지 관리</title>
    <%@ include file="/component/head.jsp" %>
    <style>
        .btn { padding: 6px 12px; cursor: pointer; border: none; border-radius: 4px; }
        .btn-edit { background-color: #4CAF50; color: white; }
        .btn-delete { background-color: #E74C3C; color: white; }
    </style>
</head>
<body>
<%@ include file="/component/header.jsp" %>
<div class="max-w-6xl flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0 py-12 mx-auto">
    <h2 class="text-2xl font-semibold mb-4">관리자 - 공지 관리</h2>

    <c:if test="${not empty error}">
        <div style="color: red; font-weight: bold;">${error}</div>
    </c:if>

    <div style="margin-bottom: 12px;">
        <a href="${pageContext.request.contextPath}/admin/notice_create.jsp" class="bg-[#FF2C00] text-white px-4 py-2 rounded-lg">공지 생성</a>
    </div>

    <table class="w-full text-left border-collapse">
        <thead>
        <tr class="border-b">
            <th class="py-2">ID</th>
            <th class="py-2">제목</th>
            <th class="py-2">생성일</th>
            <th class="py-2">관리</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="n" items="${notices}">
            <tr class="border-b">
                <td class="py-2">${n.id}</td>
                <td class="py-2">${n.title}</td>
                <td class="py-2">${n.created}</td>
                <td class="py-2">
                    <a href="${pageContext.request.contextPath}/notice/view?id=${n.id}" class="btn">보기</a>
                    <button class="btn btn-edit" onclick="openEdit('${n.id}','${n.title}','${fn:escapeXml(n.content)}')">수정</button>
                    <button class="btn btn-delete" onclick="deleteNotice('${n.id}')">삭제</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

<!-- Edit Modal (simple) -->
<div id="edit-modal" style="display:none; position:fixed; inset:0; background:rgba(0,0,0,0.5); align-items:center; justify-content:center;">
    <div style="background:white; padding:16px; width:480px; margin:auto; border-radius:8px;">
        <h3>공지 수정</h3>
        <form id="edit-form">
            <input type="hidden" id="edit-id">
            <label>제목</label>
            <input type="text" id="edit-title" style="width:100%; padding:8px; margin-bottom:8px;">
            <label>내용</label>
            <textarea id="edit-content" rows="8" style="width:100%; padding:8px;"></textarea>
            <div style="margin-top:8px; text-align:right;">
                <button type="button" onclick="closeEdit()" class="btn">취소</button>
                <button type="submit" class="btn btn-edit">저장</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openEdit(id, title, content) {
        document.getElementById('edit-id').value = id;
        document.getElementById('edit-title').value = title;
        document.getElementById('edit-content').value = content;
        document.getElementById('edit-modal').style.display = 'flex';
    }
    function closeEdit(){ document.getElementById('edit-modal').style.display='none'; }

    document.getElementById('edit-form').onsubmit = async function(e){
        e.preventDefault();
        const id = document.getElementById('edit-id').value;
        const form = new URLSearchParams();
        form.append('id', id);
        form.append('title', document.getElementById('edit-title').value);
        form.append('content', document.getElementById('edit-content').value);
        try{
            const res = await fetch('${pageContext.request.contextPath}/admin/notice', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                credentials: 'include',
                body: form
            });
            const json = await res.json();
            if(!res.ok){ alert('수정 실패: '+(json.error||'오류')); return; }
            alert(json.message);
            location.reload();
        }catch(e){ alert('서버 오류'); console.error(e); }
    };

    async function deleteNotice(id){
        if(!confirm('정말 삭제하시겠습니까?')) return;
        const form = new URLSearchParams(); form.append('id', id);
        try{
            const res = await fetch('${pageContext.request.contextPath}/admin/notice', {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                credentials: 'include',
                body: form
            });
            const json = await res.json();
            if(!res.ok){ alert('삭제 실패: '+(json.error||'오류')); return; }
            alert(json.message); location.reload();
        }catch(e){ alert('서버 오류'); console.error(e); }
    }
</script>

</body>
</html>
