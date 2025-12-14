<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
  <head>
    <title>레시피 목록</title>
    <%@ include file="/component/head.jsp" %>
  </head>
  <body class="min-h-screen">
    <div class="w-full min-h-screen flex flex-col items-center bg-gray-50">
      <%@ include file="/component/header.jsp" %>
      <div class="max-w-6xl flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0 py-12">
        <section class="w-full bg-white rounded-2xl p-8 shadow-md">
          <div class="flex justify-between items-center mb-6">
            <h1 class="text-2xl font-semibold">레시피</h1>
            <a href="${pageContext.request.contextPath}/recipe/share" class="bg-[#FF2C00] hover:bg-[#e02a00] text-white px-4 py-2 rounded-lg shadow-sm">레시피 공유</a>
          </div>

          <!-- Compact control bar: search + category -->
          <form method="get" action="${pageContext.request.contextPath}/recipe" class="mb-6 flex flex-col sm:flex-row sm:items-center gap-4">
            <div class="flex items-center gap-2 w-full sm:w-auto flex-1">
              <div class="relative w-full">
                <label for="q" class="sr-only">검색어</label>
                <input type="text" id="q" name="q" value="${q}" placeholder="제목으로 검색" class="w-full border rounded-lg px-4 py-2 pr-10 shadow-sm focus:ring-1 focus:ring-[#FF2C00]" />
                <button type="submit" class="absolute right-1 top-1/2 transform -translate-y-1/2 bg-[#FF2C00] text-white px-3 py-1 rounded-md">검색</button>
              </div>
            </div>

            <div class="flex items-center gap-3">
              <label for="category" class="text-sm text-gray-600 hidden sm:inline">카테고리</label>
              <select id="category" name="category" class="border p-2 rounded-lg shadow-sm bg-white">
                <option value="" <c:if test="${empty category or category == ''}">selected</c:if>>전체</option>
                <c:choose>
                  <c:when test="${not empty categories}">
                    <c:forEach items="${categories.entrySet()}" var="e">
                      <option value="${e.key}" <c:if test="${category == e.key}">selected</c:if>><c:out value="${e.value}"/></option>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <!-- fallback options if servlet did not provide categories -->
                    <option value="etc" <c:if test="${category == 'etc'}">selected</c:if>>기타</option>
                    <option value="egg" <c:if test="${category == 'egg'}">selected</c:if>>계란요리</option>
                    <option value="street" <c:if test="${category == 'street'}">selected</c:if>>분식</option>
                    <option value="soup" <c:if test="${category == 'soup'}">selected</c:if>>국&amp;탕</option>
                    <option value="rice" <c:if test="${category == 'rice'}">selected</c:if>>밥요리</option>
                    <option value="pasta" <c:if test="${category == 'pasta'}">selected</c:if>>파스타</option>
                    <option value="grill" <c:if test="${category == 'grill'}">selected</c:if>>구이</option>
                  </c:otherwise>
                </c:choose>
              </select>
            </div>
          </form>

          <c:if test="${empty list}">
            <div class="text-center text-gray-500 py-12">등록된 레시피가 없습니다.</div>
          </c:if>

          <!-- Recipe grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <c:forEach items="${list}" var="r">
              <div class="flex gap-4 items-start bg-white border border-gray-100 rounded-xl p-4 shadow-sm hover:shadow-md transition">
                <!-- thumbnail placeholder -->
                <div class="w-24 h-24 bg-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
                  <c:choose>
                    <c:when test="${not empty r.thumbnail}">
                      <img src="${r.thumbnail}" alt="thumb" class="w-full h-full object-cover" />
                    </c:when>
                    <c:otherwise>
                      <svg class="w-10 h-10 text-gray-300" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V7" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7a4 4 0 118 0" />
                      </svg>
                    </c:otherwise>
                  </c:choose>
                </div>

                <a href="${pageContext.request.contextPath}/recipe/view?id=${r.id}" class="flex-1 block">
                  <div class="flex justify-between items-start">
                    <h2 class="text-lg font-medium text-gray-800 truncate">${r.title}</h2>
                    <c:if test="${not empty r.category}">
                      <span style="white-space:nowrap;" class="inline-flex whitespace-nowrap items-center gap-1 text-xs px-2 py-1 rounded-full bg-[#FFF4F2] text-[#FF2C00] border border-[#FFE6E0]">
                        <c:choose>
                          <c:when test="${r.category == 'etc'}">기타</c:when>
                          <c:when test="${r.category == 'egg'}">계란요리</c:when>
                          <c:when test="${r.category == 'street'}">분식</c:when>
                          <c:when test="${r.category == 'soup'}">국&amp;탕</c:when>
                          <c:when test="${r.category == 'rice'}">밥요리</c:when>
                          <c:when test="${r.category == 'pasta'}">파스타</c:when>
                          <c:when test="${r.category == 'grill'}">구이</c:when>
                          <c:otherwise><c:out value="${r.category}"/></c:otherwise>
                        </c:choose>
                      </span>
                    </c:if>
                  </div>

                  <p class="mt-2 text-sm text-gray-600 truncate">작성자: ${authors[r.memberId].nickname}</p>

                  <div class="mt-4 text-sm text-gray-500 flex items-center justify-between">
                    <div class="flex items-center gap-4">
                      <div class="flex items-center gap-1">
                        <!-- Replaced broken icon with a standard eye icon (2-path) -->
                        <svg class="w-4 h-4 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M2.036 12.322C3.423 7.36 7.477 4 12 4c4.523 0 8.577 3.36 9.964 7.678a1 1 0 010 .644C20.577 16.64 16.523 20 12 20c-4.523 0-8.577-3.36-9.964-7.678a1 1 0 010-.644z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        </svg>
                        <span>${r.viewCount}</span>
                      </div>
                      <div class="text-xs text-gray-400">${r.created}</div>
                    </div>
                    <!-- no comment count -->
                   </div>
                 </a>
               </div>
            </c:forEach>
          </div>

          <!-- Pagination: center with Prev/Next -->
          <div class="mt-8 flex justify-center items-center gap-3">
            <c:set var="totalPage" value="${(total / limit) + (total % limit == 0 ? 0 : 1)}" />
            <c:if test="${totalPage > 1}">
              <c:set var="prev" value="${page > 1 ? page - 1 : 1}" />
              <c:set var="next" value="${page < totalPage ? page + 1 : totalPage}" />

              <a href="${pageContext.request.contextPath}/recipe?q=${fn:escapeXml(q)}&amp;page=${prev}&amp;category=${fn:escapeXml(category)}" class="px-3 py-1 rounded-md border bg-white">Prev</a>

              <div class="flex items-center gap-2">
                <c:forEach begin="1" end="${totalPage}" var="p">
                  <a href="${pageContext.request.contextPath}/recipe?q=${fn:escapeXml(q)}&amp;page=${p}&amp;category=${fn:escapeXml(category)}" class="px-3 py-1 rounded-md ${p == page ? 'bg-[#FF2C00] text-white' : 'bg-white border'}">${p}</a>
                </c:forEach>
              </div>

              <a href="${pageContext.request.contextPath}/recipe?q=${fn:escapeXml(q)}&amp;page=${next}&amp;category=${fn:escapeXml(category)}" class="px-3 py-1 rounded-md border bg-white">Next</a>
            </c:if>
          </div>
        </section>
      </div>
    </div>
  </body>
</html>
