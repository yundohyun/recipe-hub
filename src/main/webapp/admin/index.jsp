<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>관리자 대시보드</title>
  <%@ include file="/component/head.jsp" %>
</head>
<body>
<%@ include file="/component/header.jsp" %>
<div class="max-w-6xl mx-auto px-4 py-12">
  <h1 class="text-2xl font-semibold mb-6">관리자 대시보드</h1>

  <c:if test="${not empty error}">
    <div class="text-red-600 mb-4">${error}</div>
  </c:if>

  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
    <div class="bg-white p-6 rounded-xl shadow-sm">
      <div class="text-sm text-gray-500">총 회원</div>
      <div class="text-2xl font-bold">${totalMembers}</div>
      <a href="${pageContext.request.contextPath}/admin/member" class="text-sm text-[#FF2C00] mt-2 inline-block">회원 관리 보기</a>
    </div>

    <div class="bg-white p-6 rounded-xl shadow-sm">
      <div class="text-sm text-gray-500">총 레시피</div>
      <div class="text-2xl font-bold">${totalRecipes}</div>
      <a href="${pageContext.request.contextPath}/recipe" class="text-sm text-[#FF2C00] mt-2 inline-block">레시피 목록 보기</a>
    </div>

    <div class="bg-white p-6 rounded-xl shadow-sm">
      <div class="text-sm text-gray-500">총 공지</div>
      <div class="text-2xl font-bold">${totalNotices}</div>
      <a href="${pageContext.request.contextPath}/admin/notice" class="text-sm text-[#FF2C00] mt-2 inline-block">공지 관리 보기</a>
    </div>

    <div class="bg-white p-6 rounded-xl shadow-sm">
      <div class="text-sm text-gray-500">칼로리 데이터</div>
      <div class="text-2xl font-bold">${totalCalories}</div>
      <a href="${pageContext.request.contextPath}/admin/calories" class="text-sm text-[#FF2C00] mt-2 inline-block">칼로리 관리 보기</a>
    </div>
  </div>

  <section class="bg-white p-6 rounded-xl shadow-sm">
    <h2 class="text-lg font-semibold mb-2">관리 도구</h2>
    <div class="flex gap-4">
      <a href="${pageContext.request.contextPath}/admin/member" class="px-4 py-2 bg-[#FF2C00] text-white rounded-lg">회원 관리</a>
      <a href="${pageContext.request.contextPath}/admin/notice" class="px-4 py-2 bg-[#FF2C00] text-white rounded-lg">공지 관리</a>
      <a href="${pageContext.request.contextPath}/admin/calories" class="px-4 py-2 bg-[#FF2C00] text-white rounded-lg">칼로리 관리</a>
    </div>
  </section>
</div>
</body>
</html>

