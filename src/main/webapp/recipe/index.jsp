<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>레시피 목록</title>
    <%@ include file="/component/head.jsp" %>
  </head>
  <body class="min-h-screen">
    <div class="w-full min-h-screen flex flex-col items-center bg-gray-100">
      <%@ include file="/component/header.jsp" %>
      <div class="flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0 py-12">
        <section class="w-full bg-white rounded-2xl p-8 shadow-sm">
          <div class="flex justify-between items-center mb-4">
            <h1 class="text-2xl font-semibold">레시피</h1>
            <a href="${pageContext.request.contextPath}/recipe/share" class="bg-[#FF2C00] text-white px-4 py-2 rounded">레시피 공유</a>
          </div>

          <form method="get" action="${pageContext.request.contextPath}/recipe" class="mb-4">
            <input type="text" name="q" value="${q}" placeholder="제목으로 검색" class="border p-2 rounded w-72" />
            <button type="submit" class="ml-2 bg-gray-100 px-3 py-1 rounded">검색</button>
          </form>

          <c:if test="${empty list}">
            <div class="text-gray-500">등록된 레시피가 없습니다.</div>
          </c:if>

          <c:forEach items="${list}" var="r">
            <div class="border-b py-4">
              <a href="${pageContext.request.contextPath}/recipe/view?id=${r.id}" class="text-lg font-medium">${r.title}</a>
              <div class="text-sm text-gray-500">작성자: ${authors[r.memberId].nickname} · 조회수: ${r.viewCount} · 작성일: ${r.created}</div>
            </div>
          </c:forEach>

          <div class="mt-6 flex justify-center">
            <c:set var="totalPage" value="${(total / limit) + (total % limit == 0 ? 0 : 1)}" />
            <c:if test="${totalPage > 1}">
              <c:forEach begin="1" end="${totalPage}" var="p">
                <a href="${pageContext.request.contextPath}/recipe?q=${q}&page=${p}" class="px-3 py-1 border rounded mr-2 ${p == page ? 'bg-[#FF2C00] text-white' : ''}">${p}</a>
              </c:forEach>
            </c:if>
          </div>
        </section>
      </div>
    </div>
  </body>
</html>

