<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<title>내 정보 수정 - RecipeHub</title>
	<%@ include file="/component/head.jsp" %>
	<style>
		/* Profile card smooth transitions */
		.profile-card input, .profile-card textarea, .profile-card button, .profile-card a {
			transition: box-shadow .15s ease, transform .15s ease, background-color .15s ease, opacity .15s ease;
		}
		/* avatar hover scale */
		.avatar-hover { transition: transform .18s ease; }
		.avatar-hover:hover { transform: scale(1.04); }
		/* toast animation */
		#toast { transition: opacity .2s ease, transform .2s ease; }
		@keyframes spin { from { transform: rotate(0deg);} to { transform: rotate(360deg);} }
	</style>
</head>
<body class="min-h-screen">
	<div class="w-full min-h-screen flex flex-col items-center bg-gray-100">
		<%@ include file="/component/header.jsp" %>
		<div class="flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0">
			<section class="flex flex-col items-center w-full gap-y-8 py-12">
				<div class="w-full max-w-5xl bg-white rounded-2xl shadow-sm px-10 py-12 profile-card">
					<c:url var="memberUrl" value="/member" />
					<c:url var="passwordUrl" value="/password" />
					<c:url var="myUrl" value="/my" />
					<c:url var="homeUrl" value="/" />
					<c:url var="defaultAvatar" value="/assets/recipe-hub.svg" />
					<div class="flex flex-col md:flex-row md:items-start md:gap-x-8">
						<div class="flex-1">
							<span class="text-2xl font-semibold text-balance">회원 정보 수정</span>
							<p class="text-sm text-gray-500 mt-2">프로필 정보를 업데이트하면 다른 사용자에게 표시됩니다. 아바타는 JPG/PNG 권장.</p>
						</div>
					</div>
					<form action="${memberUrl}" method="post" enctype="multipart/form-data" class="mt-6 grid grid-cols-1 md:grid-cols-3 gap-6 items-start">
						<!-- Hidden fields to support MemberServlet PUT validation -->
						<input type="hidden" name="email" id="emailField" value="${member.email}" />
						<input type="hidden" name="avatar" id="avatarField" value="${member.avatar}" />
						<!-- Left: Avatar -->
						<div class="md:col-span-1 flex flex-col items-center gap-y-4">
							<div class="w-36 h-36 rounded-full overflow-hidden bg-gradient-to-br from-orange-50 via-white to-orange-50 p-1 shadow-md avatar-hover">
								<div class="w-full h-full rounded-full overflow-hidden bg-gray-100 flex items-center justify-center border border-gray-200">
									<c:choose>
										<c:when test="${not empty member and not empty member.avatar}">
											<img id="avatarPreview" src="${member.avatar}" alt="avatar" class="w-full h-full object-cover">
										</c:when>
										<c:otherwise>
											<img id="avatarPreview" src="${defaultAvatar}" alt="avatar" class="w-full h-full object-cover">
										</c:otherwise>
									</c:choose>
								</div>
							</div>
							<label class="mt-2 relative inline-flex items-center gap-x-2 cursor-pointer bg-white px-4 py-2 rounded-full border shadow-sm hover:shadow-md" style="overflow:hidden;">
								<input type="file" name="avatar" accept="image/*" id="avatarInput" style="position:absolute;inset:0;width:100%;height:100%;opacity:0;cursor:pointer;border:0;padding:0;margin:0;z-index:2;pointer-events:auto;">
								<span id="avatarBtnText" class="text-sm text-gray-700" style="position:relative;z-index:1;" role="button" tabindex="0">사진 변경</span>
							</label>
							<span class="text-xs text-gray-400 text-center">권장: 400×400, JPG/PNG</span>
							<div class="w-full mt-4 flex flex-col items-center">
								<button type="button" id="removeAvatar" class="text-sm text-red-500 hover:underline">기본 이미지로 되돌리기</button>
							</div>
						</div>
						<!-- Right: Inputs -->
						<div class="md:col-span-2 flex flex-col gap-y-4">
							<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
								<div class="flex flex-col">
									<label for="currentPassword" class="text-sm text-gray-600">현재 비밀번호</label>
									<input id="currentPassword" type="password" name="currentPassword" placeholder="현재 비밀번호" class="w-full focus:border-none focus:outline-none text-md px-4 py-2 bg-[#F5F5F5] border-[#EFEFEF] rounded-xl">
									<span class="text-xs text-gray-400">비밀번호를 변경하려면 현재 비밀번호를 입력하세요.</span>
								</div>
								<div class="flex flex-col">
									<label for="newPassword" class="text-sm text-gray-600">새 비밀번호</label>
									<input id="newPassword" type="password" name="newPassword" placeholder="새 비밀번호" class="w-full focus:border-none focus:outline-none text-md px-4 py-2 bg-[#F5F5F5] border-[#EFEFEF] rounded-xl">
									<span class="text-xs text-gray-400">8자 이상 권장</span>
								</div>
							</div>
							<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
								<div class="flex flex-col">
									<label for="confirmPassword" class="text-sm text-gray-600">새 비밀번호 확인</label>
									<input id="confirmPassword" type="password" name="confirmPassword" placeholder="새 비밀번호 확인" class="w-full focus:border-none focus:outline-none text-md px-4 py-2 bg-[#F5F5F5] border-[#EFEFEF] rounded-xl">
								</div>
								<div class="flex flex-col">
									<label for="nickname" class="text-sm text-gray-600">닉네임</label>
									<input id="nickname" type="text" name="nickname" value="${member.nickname}" placeholder="닉네임" class="w-full focus:border-none focus:outline-none text-md px-4 py-2 bg-[#F5F5F5] border-[#EFEFEF] rounded-xl">
									<span class="text-xs text-gray-400">다른 사용자에게 표시되는 이름입니다.</span>
								</div>
							</div>
							<div class="flex flex-col">
								<label for="introduce" class="text-sm text-gray-600">자기소개</label>
								<textarea id="introduce" name="introduce" rows="6" class="w-full focus:border-none focus:outline-none text-md px-4 py-2 bg-[#F5F5F5] border-[#EFEFEF] rounded-xl" placeholder="간단한 자기소개를 작성해보세요">${member.introduce}</textarea>
								<span class="text-xs text-gray-400">짧고 친절한 소개를 적어주세요.</span>
							</div>
							<!-- Actions -->
							<div class="flex flex-row gap-x-4 justify-end mt-2">
								<a href="${homeUrl}" class="px-6 py-3 rounded-xl bg-white border text-gray-700 hover:bg-gray-50">취소</a>
								<button id="saveButton" type="submit" class="px-6 py-3 rounded-xl bg-[#FF2C00] text-white hover:bg-[#FA5F29] shadow">저장</button>
							</div>
						</div>
					</form>
				</div>
				<!-- Liked recipes -->
				<div class="w-full max-w-5xl bg-white rounded-2xl shadow-sm px-10 py-8 mt-6">
					<h3 class="text-xl font-semibold mb-4">좋아요한 레시피</h3>
					<c:choose>
						<c:when test="${not empty likedRecipes}">
							<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
								<c:forEach items="${likedRecipes}" var="lr" varStatus="s">
									<div class="border rounded-lg overflow-hidden bg-white shadow-sm">
										<a href="${pageContext.request.contextPath}/recipe/view?id=${lr.id}" class="block">
											<div class="w-full h-40 bg-gray-100 overflow-hidden">
												<c:choose>
													<c:when test="${not empty lr.thumbnail}">
														<img src="${lr.thumbnail}" alt="${lr.title}" class="w-full h-full object-cover" />
													</c:when>
													<c:otherwise>
														<div class="w-full h-full flex items-center justify-center text-gray-400">No image</div>
													</c:otherwise>
												</c:choose>
											</div>
											<div class="p-3">
												<div class="font-medium text-sm text-foreground truncate">${lr.title}</div>
												<div class="text-xs text-muted-foreground mt-1">${lr.serve}인분 · ${lr.duration}분</div>
											</div>
										</a>
									</div>
								</c:forEach>
							</div>
						</c:when>
						<c:otherwise>
							<div class="text-sm text-muted-foreground">좋아요한 레시피가 없습니다.</div>
						</c:otherwise>
					</c:choose>
				</div>
				<!-- Toast -->
				<div id="toast" class="fixed right-6 bottom-6 z-50 hidden">
					<div id="toastInner" class="px-4 py-3 rounded-lg shadow-md bg-gray-800 text-white"></div>
				</div>

				<!-- Loading overlay (hidden by default) -->
				<div id="loadingOverlayMy" class="fixed inset-0 z-50 hidden flex items-center justify-center" style="background: rgba(0,0,0,0.6);">
					<div class="flex flex-col items-center gap-3">
						<div class="loader" aria-hidden="true" style="width:48px;height:48px;border-radius:50%;border:5px solid rgba(255,255,255,0.15);border-top-color:white;animation:spin 1s linear infinite;"></div>
						<div class="text-white text-md">저장 중...</div>
					</div>
				</div>
			</section>
		</div>
	</div>
</body>

<script>
  function showLoadingOverlayMy() { const o = document.getElementById('loadingOverlayMy'); if (o) o.classList.remove('hidden'); }
  function hideLoadingOverlayMy() { const o = document.getElementById('loadingOverlayMy'); if (o) o.classList.add('hidden'); }
	// Toast helper
	function showToast(message, type = 'info', timeout = 3000) {
		const toast = document.getElementById('toast');
		const inner = document.getElementById('toastInner');
		if (!toast || !inner) return;
		inner.textContent = message;
		if (type === 'error') {
			inner.className = 'px-4 py-3 rounded-lg shadow-md bg-red-600 text-white';
		} else if (type === 'success') {
			inner.className = 'px-4 py-3 rounded-lg shadow-md bg-green-600 text-white';
		} else {
			inner.className = 'px-4 py-3 rounded-lg shadow-md bg-gray-800 text-white';
		}
		toast.classList.remove('hidden');
		setTimeout(() => toast.classList.add('hidden'), timeout);
	}

	// Avatar preview
	const avatarInput = document.getElementById('avatarInput');
	const avatarPreview = document.getElementById('avatarPreview');
	let selectedAvatarFile = null;
	if (avatarInput) {
		avatarInput.addEventListener('change', function (e) {
			const file = e.target.files && e.target.files[0];
			selectedAvatarFile = file || null;
			if (!file) return;
			const reader = new FileReader();
			reader.onload = function (evt) {
				avatarPreview.src = evt.target.result;
			};
			reader.readAsDataURL(file);
		});
		// No manual label click interception — let native label->input activation work by keeping input off-screen (not display:none)
	}

	// Ensure clicking the visible label text always opens the file picker (extra click target)
	const avatarBtnText = document.getElementById('avatarBtnText');
	if (avatarBtnText && avatarInput) {
		avatarBtnText.addEventListener('click', function () { try { avatarInput.click(); } catch (err) { } });
		avatarBtnText.addEventListener('keydown', function (e) { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); try { avatarInput.click(); } catch (err) { } } });
	}

	// remove avatar (reset to default)
	const removeAvatarBtn = document.getElementById('removeAvatar');
	if (removeAvatarBtn) {
		removeAvatarBtn.addEventListener('click', function () {
			avatarPreview.src = '${defaultAvatar}';
			document.getElementById('avatarField').value = '';
		});
	}

	// Password confirm validation (UI only)
	const newPassword = document.getElementById('newPassword');
	const confirmPassword = document.getElementById('confirmPassword');
	const saveButton = document.getElementById('saveButton');
	function validatePasswords() {
		if (!newPassword || !confirmPassword || !saveButton) return;
		const a = newPassword.value;
		const b = confirmPassword.value;
		const existingMsg = document.getElementById('pwMismatch');
		if (a || b) {
			if (a !== b) {
				saveButton.disabled = true;
				saveButton.classList.add('opacity-60', 'cursor-not-allowed');
				if (!existingMsg) {
					const msg = document.createElement('div');
					msg.id = 'pwMismatch';
					msg.className = 'text-sm text-red-500 mt-2';
					msg.innerText = '새 비밀번호와 확인이 일치하지 않습니다.';
					confirmPassword.parentNode.appendChild(msg);
					showToast('새 비밀번호와 확인이 일치하지 않습니다.', 'error');
				}
				return;
			}
			// match
			saveButton.disabled = false;
			saveButton.classList.remove('opacity-60', 'cursor-not-allowed');
			if (existingMsg) existingMsg.remove();
		} else {
			// no new password entered -> enable
			saveButton.disabled = false;
			saveButton.classList.remove('opacity-60', 'cursor-not-allowed');
			if (existingMsg) existingMsg.remove();
		}
	}
	if (newPassword && confirmPassword) {
		newPassword.addEventListener('input', validatePasswords);
		confirmPassword.addEventListener('input', validatePasswords);
	}

	// AJAX form submit: 비밀번호는 /password, 프로필은 /member로 분리
	const form = document.querySelector('form');
	if (form) {
		form.addEventListener('submit', async function (e) {
			e.preventDefault();
			if (!saveButton) return;
			saveButton.disabled = true;
			saveButton.classList.add('opacity-60', 'cursor-not-allowed');
			showLoadingOverlayMy();

			const currentPw = document.getElementById('currentPassword')?.value?.trim() || '';
			const newPw = document.getElementById('newPassword')?.value?.trim() || '';
			const confirmPw = document.getElementById('confirmPassword')?.value?.trim() || '';

			try {
				let resultJson = null;
				// 1) 비밀번호 변경 요청 (필요한 경우)
				if (newPw || confirmPw) {
					if (newPw !== confirmPw) throw new Error('새 비밀번호와 확인이 일치하지 않습니다.');
					// send to /password
					const pwBody = new URLSearchParams();
					pwBody.append('currentPassword', currentPw);
					pwBody.append('newPassword', newPw);

					const pwResp = await fetch('${passwordUrl}', {
						method: 'POST',
						headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
						body: pwBody.toString(),
						credentials: 'same-origin'
					});
					const pwJson = await pwResp.json();
					if (!pwResp.ok) throw new Error(pwJson.message || '비밀번호 변경 실패');
				}

				// 2) 프로필(닉네임, 자기소개, 아바타) 업데이트
				// 파일이 선택된 경우: 명시적으로 FormData를 생성해서 파일을 append (폼 구조에 따라 파일이 누락되는 문제 방지)
				const fileSelected = selectedAvatarFile != null;
				if (fileSelected) {
					const fd = new FormData();
					fd.append('email', document.getElementById('emailField')?.value || '');
					fd.append('nickname', document.getElementById('nickname')?.value || '');
					fd.append('introduce', document.getElementById('introduce')?.value || '');
					// append selected file
					fd.append('avatar', selectedAvatarFile, selectedAvatarFile.name);

					const updateResp = await fetch('${memberUrl}', {
						method: 'POST',
						body: fd,
						credentials: 'same-origin'
					});
					const updateJson = await updateResp.json();
					resultJson = updateJson;
					if (!updateResp.ok) throw new Error(updateJson.message || '업데이트 실패');
				} else {
					// send PUT to /member with URL-encoded body (MemberServlet.doPut expects URL-encoded body)
					const body = new URLSearchParams();
					body.append('email', document.getElementById('emailField')?.value || '');
					body.append('nickname', document.getElementById('nickname')?.value || '');
					// avatar field: keep existing URL
					body.append('avatar', document.getElementById('avatarField')?.value || '');
					body.append('introduce', document.getElementById('introduce')?.value || '');

					const memberResp = await fetch('${memberUrl}', {
						method: 'PUT',
						headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
						body: body.toString(),
						credentials: 'same-origin'
					});
					const memberJson = await memberResp.json();
					resultJson = memberJson;
					if (!memberResp.ok) throw new Error(memberJson.message || '업데이트 실패');
				}

				// 성공: 토스트를 띄우고 약간 대기 후 리디렉션
				showToast((resultJson && resultJson.message) || '저장이 완료되었습니다.', 'success', 1200);
				setTimeout(function () { window.location.href = '${myUrl}'; }, 900);

			} catch (err) {
				// 사용자에게 에러 메시지 표시
				let msg = err?.message || '오류가 발생했습니다.';
				showToast(msg, 'error');
			} finally {
					hideLoadingOverlayMy();
					saveButton.disabled = false;
					saveButton.classList.remove('opacity-60', 'cursor-not-allowed');
				}
			});
		}
</script>
</html>
