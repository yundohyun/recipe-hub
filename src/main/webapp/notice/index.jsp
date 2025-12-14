<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
  <title>공지사항</title>
  <%@ include file="/component/head.jsp" %>
</head>
<body class="min-h-screen">
  <div class="w-full min-h-screen flex flex-col items-center bg-gray-50">
    <%@ include file="/component/header.jsp" %>
    <div class="max-w-6xl flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0 py-12">
      <section class="w-full bg-white rounded-2xl p-8 shadow-md">
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-2xl font-semibold">공지사항</h1>
          <c:if test="${sessionScope.isAdmin == true}">
            <a href="${pageContext.request.contextPath}/admin/notice" class="bg-[#FF2C00] hover:bg-[#e02a00] text-white px-4 py-2 rounded-lg shadow-sm">관리자</a>
          </c:if>
        </div>

        <c:if test="${not empty error}">
          <div class="text-red-600 font-bold mb-4">${error}</div>
        </c:if>

        <c:if test="${empty list}">
          <div class="text-center text-gray-500 py-12">등록된 공지가 없습니다.</div>
        </c:if>

        <div class="divide-y">
          <c:forEach items="${list}" var="n">
            <div class="py-4">
              <a href="${pageContext.request.contextPath}/notice/view?id=${n.id}" class="block">
                <div class="flex justify-between items-center">
                  <h2 class="text-lg font-medium text-gray-800">${n.title}</h2>
                  <div class="text-sm text-gray-400">${n.created}</div>
                </div>
                <p class="mt-2 text-sm text-gray-600 truncate">${fn:escapeXml(n.content)}</p>
              </a>
            </div>
          </c:forEach>
        </div>

      </section>
    </div>
  </div>
</body>
</html>
