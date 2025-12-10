<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>레시피 공유</title>
    <%@ include file="/component/head.jsp" %>
  </head>
  <body class="min-h-screen">
    <div class="w-full min-h-screen flex flex-col items-center bg-gray-100">
      <%@ include file="/component/header.jsp" %>
      <div class="flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0 py-12">
        <section class="w-full bg-white rounded-2xl p-8 shadow-sm">
          <h1 class="text-2xl font-semibold mb-4">레시피 공유하기</h1>

          <c:if test="${not empty error}">
            <div class="text-red-500 mb-4">${error}</div>
          </c:if>

          <form action="${pageContext.request.contextPath}/recipe/share" method="post" enctype="multipart/form-data" id="shareForm">
            <div class="mb-4">
              <label class="block mb-2">제목</label>
              <input type="text" name="title" class="w-full border p-2 rounded" />
            </div>

            <div class="grid grid-cols-2 gap-4 mb-4">
              <div>
                <label class="block mb-2">인원수</label>
                <input type="number" name="serve" class="w-full border p-2 rounded" />
              </div>
              <div>
                <label class="block mb-2">소요시간(분)</label>
                <input type="number" name="duration" class="w-full border p-2 rounded" />
              </div>
            </div>

            <div class="mb-4">
              <label class="block mb-2">칼로리 선택 (옵션, id 입력)</label>
              <input type="text" name="caloriesId" class="w-full border p-2 rounded" placeholder="칼로리 id를 입력하세요 (선택)" />
            </div>

            <div class="mb-4">
              <label class="block mb-2">재료</label>
              <div id="ingredientsRoot">
                <div class="flex gap-2 mb-2">
                  <input type="text" name="ingredient[]" placeholder="재료 이름" class="flex-1 border p-2 rounded" />
                  <input type="text" name="amount[]" placeholder="양 (예: 1컵)" class="w-40 border p-2 rounded" />
                  <button type="button" onclick="removeRow(this)" class="text-red-500">삭제</button>
                </div>
              </div>
              <button type="button" onclick="addIngredient()" class="mt-2 bg-gray-100 px-3 py-1 rounded">재료 추가</button>
            </div>

            <div class="mb-4">
              <label class="block mb-2">조리 순서</label>
              <div id="stepsRoot">
                <div class="mb-4 stepRow">
                  <textarea name="content[]" rows="3" class="w-full border p-2 rounded" placeholder="조리 방법"></textarea>
                  <div class="mt-2">
                    <label>이미지 (선택)</label>
                    <input type="file" name="contentImage[]" accept="image/*" />
                  </div>
                  <div class="mt-2">
                    <button type="button" onclick="removeStep(this)" class="text-red-500">삭제</button>
                  </div>
                </div>
              </div>
              <button type="button" onclick="addStep()" class="mt-2 bg-gray-100 px-3 py-1 rounded">단계 추가</button>
            </div>

            <div class="flex gap-4">
              <button type="submit" class="bg-[#FF2C00] text-white px-6 py-2 rounded">공유하기</button>
              <a href="${pageContext.request.contextPath}/" class="px-6 py-2 border rounded">취소</a>
            </div>
          </form>
        </section>
      </div>
    </div>

    <script>
      function addIngredient() {
        const root = document.getElementById('ingredientsRoot');
        const div = document.createElement('div');
        div.className = 'flex gap-2 mb-2';
        div.innerHTML = `
          <input type="text" name="ingredient[]" placeholder="재료 이름" class="flex-1 border p-2 rounded" />
          <input type="text" name="amount[]" placeholder="양 (예: 1컵)" class="w-40 border p-2 rounded" />
          <button type="button" onclick="removeRow(this)" class="text-red-500">삭제</button>
        `;
        root.appendChild(div);
      }

      function removeRow(btn) {
        btn.parentElement.remove();
      }

      function addStep() {
        const root = document.getElementById('stepsRoot');
        const div = document.createElement('div');
        div.className = 'mb-4 stepRow';
        div.innerHTML = `
          <textarea name="content[]" rows="3" class="w-full border p-2 rounded" placeholder="조리 방법"></textarea>
          <div class="mt-2">
            <label>이미지 (선택)</label>
            <input type="file" name="contentImage[]" accept="image/*" />
          </div>
          <div class="mt-2">
            <button type="button" onclick="removeStep(this)" class="text-red-500">삭제</button>
          </div>
        `;
        root.appendChild(div);
      }

      function removeStep(btn) {
        btn.parentElement.parentElement.remove();
      }
    </script>
  </body>
</html>

