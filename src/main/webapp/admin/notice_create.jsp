<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>공지 생성</title>
    <%@ include file="/component/head.jsp" %>
</head>
<body>
<%@ include file="/component/header.jsp" %>
<div class="max-w-6xl flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0 py-12 mx-auto">
    <h2 class="text-2xl font-semibold mb-4">공지 생성</h2>

    <form id="create-form">
        <label>제목</label>
        <input type="text" id="title" style="width:100%; padding:8px; margin-bottom:8px;">
        <label>내용</label>
        <textarea id="content" rows="10" style="width:100%; padding:8px;"></textarea>
        <div style="margin-top:8px; text-align:right;">
            <a href="${pageContext.request.contextPath}/admin/notice" class="btn">취소</a>
            <button type="submit" class="btn btn-edit">생성</button>
        </div>
    </form>
</div>

<script>
    document.getElementById('create-form').onsubmit = async function(e){
        e.preventDefault();
        const form = new URLSearchParams();
        form.append('title', document.getElementById('title').value);
        form.append('content', document.getElementById('content').value);
        try{
            const res = await fetch('${pageContext.request.contextPath}/admin/notice', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                credentials: 'include',
                body: form
            });
            const json = await res.json();
            if(!res.ok){ alert('생성 실패: '+(json.error||'오류')); return; }
            alert(json.message);
            location.href='${pageContext.request.contextPath}/admin/notice';
        }catch(e){ alert('서버 오류'); console.error(e); }
    };
</script>
</body>
</html>

