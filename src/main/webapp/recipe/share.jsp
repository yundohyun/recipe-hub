<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>레시피 공유 - RecipeHub</title>
    <%@ include file="/component/head.jsp" %>
  </head>
  <body class="min-h-screen bg-gray-50 text-slate-900">
    <%@ include file="/component/header.jsp" %>

    <div class="max-w-4xl mx-auto px-4 py-12">
      <div class="mb-12">
        <h1 class="text-4xl font-bold text-foreground mb-2">새 레시피 작성</h1>
        <p class="text-muted-foreground text-lg">당신의 맛있는 레시피를 다른 사용자들과 공유해 보세요.</p>
      </div>

      <form id="shareForm" action="${pageContext.request.contextPath}/recipe/share" method="post" enctype="multipart/form-data" class="space-y-8">

        <!-- Recipe Image -->
        <div class="text-card-foreground flex flex-col gap-6 rounded-xl border shadow-sm bg-card border-border p-8">
          <h2 class="text-2xl font-bold text-foreground mb-4">레시피 이미지</h2>
          <div class="space-y-4">
            <label id="thumbnailLabel" for="thumbnailInput" class="flex items-center justify-center w-full p-8 border-2 border-dashed border-border rounded-lg hover:border-primary/50 cursor-pointer transition bg-muted/30">
              <span class="flex flex-col items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-muted-foreground" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" x2="12" y1="3" y2="15"></line></svg>
                <span class="text-foreground font-medium">이미지 업로드</span>
                <span class="text-sm text-muted-foreground">또는 드래그하여 놓기</span>
              </span>
            </label>
            <input id="thumbnailInput" name="thumbnail" accept="image/*" type="file" class="hidden" />
            <div id="thumbnailPreview" class="mt-2"></div>
          </div>
        </div>

        <!-- Basic info -->
        <div class="text-card-foreground flex flex-col gap-6 rounded-xl border shadow-sm bg-card border-border p-8">
          <h2 class="text-2xl font-bold text-foreground mb-4">기본 정보</h2>

          <div class="space-y-6">
            <div class="space-y-2">
              <label for="title" class="text-sm font-semibold text-foreground">레시피 제목</label>
              <input id="title" name="title" type="text" required class="w-full rounded-md border border-border px-3 py-2 bg-input text-lg" placeholder="예: 초간단 계란말이" />
            </div>

            <div class="space-y-2">
              <label for="description" class="text-sm font-semibold text-foreground">레시피 설명</label>
              <textarea id="description" name="description" rows="4" required class="w-full px-4 py-2 rounded-lg bg-input border border-border text-foreground" placeholder="이 레시피에 대해 설명해주세요..."></textarea>
            </div>

            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div class="space-y-2">
                <label for="servings" class="text-sm font-semibold text-foreground">인분</label>
                <input id="servings" name="serve" type="number" min="1" value="2" class="w-full rounded-md border border-border px-3 py-2 bg-input" />
              </div>

              <div class="space-y-2">
                <label for="time" class="text-sm font-semibold text-foreground">소요시간</label>
                <input id="time" name="duration" type="text" required class="w-full rounded-md border border-border px-3 py-2 bg-input" placeholder="예: 30분" />
              </div>

              <div class="space-y-2">
                <label for="difficulty" class="text-sm font-semibold text-foreground">난이도</label>
                <select id="difficulty" name="difficulty" class="w-full rounded-md border border-border px-3 py-2 bg-input">
                  <option value="easy">쉬움</option>
                  <option value="medium">중간</option>
                  <option value="hard">어려움</option>
                </select>
              </div>

              <div class="space-y-2">
                <label for="category" class="text-sm font-semibold text-foreground">카테고리</label>
                <select id="category" name="category" required class="w-full rounded-md border border-border px-3 py-2 bg-input">
                  <option value="etc">기타</option>
                  <option value="egg">계란요리</option>
                  <option value="street">분식</option>
                  <option value="soup">국&amp;탕</option>
                  <option value="rice">밥요리</option>
                  <option value="pasta">파스타</option>
                  <option value="grill">구이</option>
                </select>
              </div>
            </div>

          </div>
        </div>

        <!-- Ingredients (dynamic) : name + amount -->
        <div class="text-card-foreground flex flex-col gap-6 rounded-xl border shadow-sm bg-card border-border p-8">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-2xl font-bold text-foreground">재료</h2>
            <button id="addIngredientBtn" type="button" class="inline-flex items-center gap-2 bg-primary text-primary-foreground px-3 py-1 rounded-md">+ 재료 추가</button>
          </div>

          <div id="ingredientsRoot" class="space-y-3">
            <div class="flex gap-2 items-center">
              <input name="ingredient[]" aria-label="재료 이름" placeholder="예: 계란" class="flex-1 rounded-md border border-border px-3 py-2 bg-input" />
              <input name="amount[]" aria-label="재료 양" placeholder="예: 2개" class="w-36 rounded-md border border-border px-3 py-2 bg-input" />
              <button type="button" onclick="this.parentElement.remove()" class="text-red-500">삭제</button>
            </div>
          </div>
        </div>

        <!-- Steps (dynamic) -->
        <div class="text-card-foreground flex flex-col gap-6 rounded-xl border shadow-sm bg-card border-border p-8">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-2xl font-bold text-foreground">조리 방법</h2>
            <button id="addStepBtn" type="button" class="inline-flex items-center gap-2 bg-primary text-primary-foreground px-3 py-1 rounded-md">+ 단계 추가</button>
          </div>

          <div id="stepsRoot" class="space-y-6">
            <div class="border-l-4 border-primary/30 pl-6 pb-6">
              <div class="flex items-center gap-3 mb-4">
                <span class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-primary text-primary-foreground font-bold">1</span>
                <label class="text-lg font-semibold text-foreground">단계 1</label>
              </div>

              <div class="space-y-4">
                <textarea name="content[]" aria-label="조리 과정" placeholder="조리 과정을 설명해주세요..." class="w-full px-4 py-2 rounded-lg bg-input border border-border text-foreground" rows="3"></textarea>

                <label class="flex items-center justify-center w-full p-6 border-2 border-dashed border-border rounded-lg hover:border-primary/50 cursor-pointer transition bg-muted/20">
                  <div class="flex items-center gap-2">
                    <svg class="w-6 h-6 text-muted-foreground" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"></rect><circle cx="9" cy="9" r="2"></circle><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"></path></svg>
                    <span class="text-sm text-foreground font-medium">단계 사진 추가</span>
                    <span class="text-xs text-muted-foreground">선택사항</span>
                  </div>
                  <input name="contentImage_1" accept="image/*" type="file" class="hidden content-image-input" />
                </label>
                <!-- preview placeholder for this step -->
                <div class="mt-2 content-image-preview"></div>

              </div>
            </div>
          </div>
        </div>

        <!-- Nutrition -->
<%--        <div class="text-card-foreground flex flex-col gap-6 rounded-xl border shadow-sm bg-card border-border p-8">--%>
<%--          <div class="flex items-center gap-3 mb-4">--%>
<%--            <svg class="w-6 h-6 text-primary" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"></path></svg>--%>
<%--            <h2 class="text-2xl font-bold text-foreground">영양 정보 (1인분)</h2>--%>
<%--          </div>--%>

<%--          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">--%>
<%--            <div class="space-y-2">--%>
<%--              <label class="text-sm font-semibold text-foreground" for="calories">칼로리</label>--%>
<%--              <div class="flex items-center gap-2">--%>
<%--                <input id="calories" name="calories" class="w-full rounded-md border border-border px-3 py-2 bg-input" placeholder="예: 250" />--%>
<%--                <span class="text-xs text-muted-foreground">kcal</span>--%>
<%--              </div>--%>
<%--            </div>--%>

<%--            <div class="space-y-2">--%>
<%--              <label class="text-sm font-semibold text-foreground" for="protein">단백질</label>--%>
<%--              <div class="flex items-center gap-2">--%>
<%--                <input id="protein" name="protein" class="w-full rounded-md border border-border px-3 py-2 bg-input" placeholder="예: 15" />--%>
<%--                <span class="text-xs text-muted-foreground">g</span>--%>
<%--              </div>--%>
<%--            </div>--%>

<%--            <div class="space-y-2">--%>
<%--              <label class="text-sm font-semibold text-foreground" for="fat">지방</label>--%>
<%--              <div class="flex items-center gap-2">--%>
<%--                <input id="fat" name="fat" class="w-full rounded-md border border-border px-3 py-2 bg-input" placeholder="예: 8" />--%>
<%--                <span class="text-xs text-muted-foreground">g</span>--%>
<%--              </div>--%>
<%--            </div>--%>

<%--            <div class="space-y-2">--%>
<%--              <label class="text-sm font-semibold text-foreground" for="carbs">탄수화물</label>--%>
<%--              <div class="flex items-center gap-2">--%>
<%--                <input id="carbs" name="carbs" class="w-full rounded-md border border-border px-3 py-2 bg-input" placeholder="예: 30" />--%>
<%--                <span class="text-xs text-muted-foreground">g</span>--%>
<%--              </div>--%>
<%--            </div>--%>
<%--          </div>--%>

<%--          <div class="mt-6 p-4 bg-muted/50 rounded-lg flex gap-3">--%>
<%--            <svg class="w-5 h-5 text-primary flex-shrink-0" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" x2="12" y1="8" y2="12"></line><line x1="12" x2="12.01" y1="16" y2="16"></line></svg>--%>
<%--            <p class="text-sm text-muted-foreground">영양 정보는 선택사항입니다. 정확한 정보를 입력하면 사용자들이 더 좋아할 거예요.</p>--%>
<%--          </div>--%>
<%--        </div>--%>

        <!-- Tags only (tips removed) -->
<%--        <div class="text-card-foreground flex flex-col gap-6 rounded-xl border shadow-sm bg-card border-border p-8">--%>
<%--          <h2 class="text-2xl font-bold text-foreground mb-4">태그</h2>--%>
<%--          <div class="space-y-3">--%>
<%--            <div class="flex items-center justify-between mb-2">--%>
<%--              <input id="tagsRootInput" name="tags[]" class="w-full rounded-md border border-border px-3 py-2 bg-input" placeholder="예: 초간단, 10분요리" />--%>
<%--              <button id="addTagBtn" type="button" class="ml-3 inline-flex items-center gap-2 bg-primary text-primary-foreground px-3 py-1 rounded-md">+ 추가</button>--%>
<%--            </div>--%>
<%--            <div id="tagsRoot" class="space-y-2"></div>--%>
<%--          </div>--%>
<%--        </div>--%>

        <!-- Actions (match header buttons) -->
        <div class="flex gap-4">
          <a href="${pageContext.request.contextPath}/recipe" class="flex-1">
            <span role="button" aria-label="취소" class="inline-flex items-center justify-center w-full h-10 rounded-xl px-4 py-2 hover:bg-[#FA5F29] hover:text-white transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-ring/50">
              <!-- X icon -->
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              취소
            </span>
          </a>
          <button type="submit" class="flex-1 inline-flex items-center justify-center gap-2 h-10 rounded-xl px-4 py-2 bg-[#FF2C00] text-white hover:bg-[#FA5F29] shadow-sm transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-ring/50" aria-label="레시피 게시">
            <!-- Upload icon -->
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" x2="12" y1="3" y2="15"></line></svg>
            레시피 게시
          </button>
        </div>

      </form>
    </div>

    <!-- Loading overlay (hidden by default) -->
    <div id="loadingOverlayShare" class="fixed inset-0 z-50 hidden flex items-center justify-center" style="background: rgba(0,0,0,0.6);">
      <div class="flex flex-col items-center gap-3">
        <div class="loader" aria-hidden="true" style="width:64px;height:64px;border-radius:50%;border:6px solid rgba(255,255,255,0.15);border-top-color:white;animation:spin 1s linear infinite;"></div>
        <div class="text-white text-lg">업로드 중...</div>
      </div>
    </div>

    <style>
      @keyframes spin { from { transform: rotate(0deg);} to { transform: rotate(360deg);} }
    </style>

    <script>
      // thumbnail preview and click proxy
      function showLoadingOverlayShare() {
        const o = document.getElementById('loadingOverlayShare');
        if (o) o.classList.remove('hidden');
      }
      function hideLoadingOverlayShare() {
        const o = document.getElementById('loadingOverlayShare');
        if (o) o.classList.add('hidden');
      }
      const thumbnailInput = document.getElementById('thumbnailInput');
      const thumbnailPreview = document.getElementById('thumbnailPreview');
      // label already uses 'for="thumbnailInput"' — no extra click proxy to avoid double dialog

      if (thumbnailInput) {
        thumbnailInput.addEventListener('change', (e) => {
          thumbnailPreview.innerHTML = '';
          const f = e.target.files && e.target.files[0];
          if (!f) return;
          const img = document.createElement('img');
          img.className = 'w-40 h-40 object-cover rounded';
          img.src = URL.createObjectURL(f);
          thumbnailPreview.appendChild(img);
        });
      }

      // dynamic ingredients
      document.getElementById('addIngredientBtn').addEventListener('click', () => {
        const root = document.getElementById('ingredientsRoot');
        const div = document.createElement('div');
        div.className = 'flex gap-2 items-center';
        div.innerHTML = `
          <input name="ingredient[]" aria-label="재료 이름" placeholder="예: 계란" class="flex-1 rounded-md border border-border px-3 py-2 bg-input" />
          <input name="amount[]" aria-label="재료 양" placeholder="예: 2개" class="w-36 rounded-md border border-border px-3 py-2 bg-input" />
          <button type="button" onclick="this.parentElement.remove()" class="text-red-500">삭제</button>
        `;
        root.appendChild(div);
      });

      // dynamic steps
      let stepCount = 1;
      document.getElementById('addStepBtn').addEventListener('click', () => {
        stepCount++;
        const root = document.getElementById('stepsRoot');
        const div = document.createElement('div');
        div.className = 'border-l-4 border-primary/30 pl-6 pb-6';
        div.innerHTML = `
             <div class="flex items-center gap-3 mb-4">
             <span class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-primary text-primary-foreground font-bold">\${stepCount}</span>
             <label class="text-lg font-semibold text-foreground">단계 \${stepCount}</label>
              <button type="button" onclick="this.closest('.border-l-4').remove()" class="ml-auto text-red-500">삭제</button>
            </div>
             <div class="space-y-4">
             <textarea name="content[]" aria-label="조리 과정" placeholder="조리 과정을 설명해주세요..." class="w-full px-4 py-2 rounded-lg bg-input border border-border text-foreground" rows="3"></textarea>

              <label class="flex items-center justify-center w-full p-6 border-2 border-dashed border-border rounded-lg hover:border-primary/50 cursor-pointer transition bg-muted/20">
                <div class="flex items-center gap-2">
                  <svg class="w-6 h-6 text-muted-foreground" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"></rect><circle cx="9" cy="9" r="2"></circle><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"></path></svg>
                  <span class="text-sm text-foreground font-medium">단계 사진 추가</span>
                  <span class="text-xs text-muted-foreground">선택사항</span>
                </div>
               <input accept="image/*" type="file" class="hidden content-image-input" />
              </label>
              <div class="mt-2 content-image-preview"></div>
            </div>
          `;
          root.appendChild(div);
         // set name and attach preview listener for the newly added file input
         const newInput = div.querySelector('input.content-image-input');
         if (newInput) {
           newInput.name = 'contentImage_' + stepCount;
           attachContentImagePreview(newInput);
         }
        });

       // attach preview handler for a content image input
       function attachContentImagePreview(input) {
         if (!input) return;
         // find or create preview container immediately after label
         let label = input.closest('label');
         let preview = label ? label.nextElementSibling : null;
         if (!preview || !preview.classList.contains('content-image-preview')) {
           preview = document.createElement('div');
           preview.className = 'mt-2 content-image-preview';
           if (label) label.insertAdjacentElement('afterend', preview);
         }
         input.addEventListener('change', (e) => {
           preview.innerHTML = '';
           const f = e.target.files && e.target.files[0];
           if (!f) return;
           const img = document.createElement('img');
           img.className = 'w-36 h-36 object-cover rounded';
           img.src = URL.createObjectURL(f);
           preview.appendChild(img);
         });
       }

       // initialize preview listeners for any existing content image inputs
       document.querySelectorAll('input.content-image-input, input[name^="contentImage_"]').forEach((inp) => {
         attachContentImagePreview(inp);
       });

      // Intercept form submit and upload via fetch so we can show a success alert and redirect
      const shareForm = document.getElementById('shareForm');
      if (shareForm) {
        shareForm.addEventListener('submit', async function (e) {
          e.preventDefault();
          showLoadingOverlayShare();
          const submitBtn = shareForm.querySelector('button[type="submit"]');
           // preserve original button content
           const origHtml = submitBtn ? submitBtn.innerHTML : null;
           if (submitBtn) {
             submitBtn.disabled = true;
             submitBtn.innerHTML = '업로드 중...';
           }
           try {
             const fd = new FormData(shareForm);
             const response = await fetch(shareForm.action, {
               method: 'POST',
               body: fd,
               credentials: 'same-origin'
             });

            // If server redirected, fetch will follow; response.url will differ from the request URL
            const requestedUrl = new URL(shareForm.action, location.origin).href;
            if (response.redirected || (response.url && response.url !== requestedUrl)) {
              alert('레시피 업로드가 완료되었습니다.');
              // go to recipe list page
              window.location.href = '${pageContext.request.contextPath}/recipe';
              return;
            }

            // If no redirect, try to inspect response text for an error (server forwards back to share.jsp on error)
            const text = await response.text();
            // simple heuristic: if returned HTML contains '레시피 생성 중 오류' or 'error', show it.
            if (/레시피 생성 중 오류|error/i.test(text)) {
              alert('레시피 생성 중 오류가 발생했습니다. 내용을 확인해주세요.');
            } else {
              // Fallback: assume success (some servers may return 200 with page content)
              alert('레시피 업로드가 완료되었습니다.');
              window.location.href = '${pageContext.request.contextPath}/recipe';
            }
           } catch (err) {
             console.error('Upload error', err);
             alert('업로드 중 오류가 발생했습니다. 네트워크 또는 서버 상태를 확인하세요.');
           } finally {
             hideLoadingOverlayShare();
             if (submitBtn) {
               submitBtn.disabled = false;
               submitBtn.innerHTML = origHtml;
             }
           }
         });
       }
    </script>
  </body>
</html>
