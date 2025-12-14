<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>공지사항 보기</title>
  <%@ include file="/component/head.jsp" %>
</head>
<body class="min-h-screen">
  <div class="w-full min-h-screen flex flex-col items-center bg-gray-50">
    <%@ include file="/component/header.jsp" %>
    <div class="max-w-6xl flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0 py-12">
      <section class="w-full bg-white rounded-2xl p-8 shadow-md">
        <div class="flex justify-between items-center mb-4">
          <h1 class="text-2xl font-semibold">공지사항</h1>
          <a href="${pageContext.request.contextPath}/notice" class="text-sm text-gray-500">목록으로</a>
        </div>

        <c:if test="${not empty error}">
          <div class="text-red-600 font-bold mb-4">${error}</div>
        </c:if>

        <c:if test="${not empty notice}">
          <h2 class="text-xl font-semibold">${notice.title}</h2>
          <div class="text-sm text-gray-400 mt-2">${notice.created}</div>
          <div class="mt-6 text-gray-700 whitespace-pre-line">${notice.content}</div>
        </c:if>

      </section>
    </div>
  </div>
</body>
</html>
