<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>레시피 보기</title>
    <%@ include file="/component/head.jsp" %>
  </head>
  <body class="min-h-screen">
    <div class="w-full min-h-screen flex flex-col items-center bg-gray-100">
      <%@ include file="/component/header.jsp" %>
      <div class="flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0 py-12">
        <section class="w-full bg-white rounded-2xl p-8 shadow-sm">
          <h1 class="text-2xl font-semibold mb-2">${recipe.title}</h1>
          <div class="text-sm text-gray-500 mb-4">작성자: ${author.nickname}</div>
          <div class="grid grid-cols-2 gap-4 mb-4">
            <div>인원수: ${recipe.serve}</div>
            <div>소요시간: ${recipe.duration} 분</div>
          </div>

          <c:if test="${not empty recipeCalories}">
            <div class="mb-4">칼로리 정보: ${recipeCalories.calories} kcal (제공량: ${recipeCalories.serve})</div>
          </c:if>

          <div class="mb-4">
            <h2 class="font-medium">재료</h2>
            <ul class="list-disc ml-6">
              <c:forEach items="${ingredients}" var="ing">
                <li>${ing.ingredient} ${ing.amount != null ? '(' + ing.amount + ')' : ''}</li>
              </c:forEach>
            </ul>
          </div>

          <div>
            <h2 class="font-medium">조리순서</h2>
            <c:forEach items="${contents}" var="step">
              <div class="mb-4">
                <div class="font-medium">Step ${step.step}</div>
                <div class="mt-2 whitespace-pre-wrap">${step.content}</div>
                <c:if test="${not empty contentImages[step.id]}">
                  <div class="flex gap-2 mt-2">
                    <c:forEach items="${contentImages[step.id]}" var="img">
                      <img src="${img.image}" class="w-40 h-40 object-cover rounded" />
                    </c:forEach>
                  </div>
                </c:if>
              </div>
            </c:forEach>
          </div>

          <div class="mt-6">
            <a href="${pageContext.request.contextPath}/" class="px-4 py-2 border rounded">목록</a>
          </div>
        </section>
      </div>
    </div>
  </body>
</html>

