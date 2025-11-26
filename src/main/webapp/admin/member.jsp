<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 - 회원 관리</title>

    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: center; }
        th { background-color: #f4f4f4; }
        .btn { padding: 6px 12px; cursor: pointer; border: none; border-radius: 4px; }
        .btn-edit { background-color: #4CAF50; color: white; }
        .btn-delete { background-color: #E74C3C; color: white; }
        .modal-bg {
            display: none; position: fixed; top: 0; left: 0;
            width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5);
            justify-content: center; align-items: center;
        }
        .modal {
            background: white; padding: 20px; width: 350px;
            border-radius: 6px;
        }
        .modal input, .modal textarea {
            width: 100%; margin-top: 8px; margin-bottom: 12px; padding: 6px;
        }
        .btn-close {
            background-color: gray; color: white; float: right;
        }
    </style>
</head>

<body>

<h2>관리자 - 회원 관리</h2>

<c:if test="${not empty error}">
    <div style="color: red; font-weight: bold;">${error}</div>
</c:if>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>닉네임</th>
        <th>아바타</th>
        <th>소개</th>
        <th>관리자 여부</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="m" items="${members}">
        <tr>
            <td>${m.id}</td>
            <td>${m.nickname}</td>
            <td><img src="${m.avatar}" width="40"></td>
            <td>${m.introduce}</td>
            <td>${m.admin ? '✔' : '✘'}</td>
            <td>
                <button class="btn btn-edit"
                        onclick="openEditModal('${m.id}', '${m.nickname}', '${m.avatar}', `${m.introduce}`)">
                    수정
                </button>
                <button class="btn btn-delete" onclick="deleteMember('${m.id}')">삭제</button>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>


<!-- ============================
        수정 모달창
 ============================ -->
<div class="modal-bg" id="edit-modal">
    <div class="modal">
        <h3>회원 정보 수정</h3>

        <button class="btn btn-close" onclick="closeModal()">닫기</button>

        <form id="edit-form">
            <input type="hidden" id="edit-id">

            <label>닉네임</label>
            <input type="text" id="edit-nickname">

            <label>아바타 URL</label>
            <input type="text" id="edit-avatar">

            <label>소개</label>
            <textarea id="edit-introduction" rows="4"></textarea>

            <button type="submit" class="btn btn-edit" style="margin-top: 10px;">수정 적용</button>
        </form>
    </div>
</div>


<script>
    // --- 수정 모달 열기 ---
    function openEditModal(id, nickname, avatar, introduction) {
        document.getElementById("edit-id").value = id;
        document.getElementById("edit-nickname").value = nickname;
        document.getElementById("edit-avatar").value = avatar;
        document.getElementById("edit-introduction").value = introduction;

        document.getElementById("edit-modal").style.display = "flex";
    }

    function closeModal() {
        document.getElementById("edit-modal").style.display = "none";
    }

    // ===============================
    //       PUT: 회원 수정 요청
    // ===============================
    document.getElementById("edit-form").onsubmit = async function (e) {
        e.preventDefault();

        const id = document.getElementById("edit-id").value;

        const formData = new URLSearchParams();
        formData.append("memberId", id);
        formData.append("nickname", document.getElementById("edit-nickname").value);
        formData.append("avatar", document.getElementById("edit-avatar").value);
        formData.append("introduction", document.getElementById("edit-introduction").value);

        try {
            const res = await fetch("<c:url value='/admin/member'/>", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                credentials: "include",
                body: formData
            });

            const json = await res.json();

            if (!res.ok) {
                alert("❌ 수정 실패: " + (json.error || "알 수 없는 오류"));
                return;
            }

            alert("✔ 수정 성공: " + json.message);
            location.reload();

        } catch (e) {
            alert("서버 오류 발생");
            console.error(e);
        }
    };



    // ===============================
    //       DELETE: 회원 삭제 요청
    // ===============================
    async function deleteMember(id) {
        if (!confirm("정말 삭제하시겠습니까?")) return;

        const formData = new URLSearchParams();
        formData.append("memberId", id);

        try {
            const res = await fetch("<c:url value='/admin/member'/>", {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                credentials: "include",
                body: formData
            });

            const json = await res.json();

            if (!res.ok) {
                alert("❌ 삭제 실패: " + (json.error || "알 수 없는 오류"));
                return;
            }

            alert("✔ 삭제 성공: " + json.message);
            location.reload();

        } catch (e) {
            alert("서버 오류 발생");
            console.error(e);
        }
    }
</script>

</body>
</html>
