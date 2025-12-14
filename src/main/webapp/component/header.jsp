<%@ page import="java.util.Objects" %>
<%@ page pageEncoding="utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="flex flex-row justify-center w-full h-16 bg-white shadow-sm">
	<div class="flex flex-row justify-between w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0">
		<a href="${pageContext.request.contextPath}/" class="flex flex-row items-center gap-x-2 xl:gap-x-4">
			<img src="${pageContext.request.contextPath}/assets/recipe-hub.svg" alt="RecipeHub" class="w-8 h-8">
			<span class="font-semibold text-lg">RecipeHub</span>
		</a>
		<div class="flex flex-row items-center gap-x-3 md:gap-x-4">
			<% if (
				Objects.equals(request.getRequestURI(), "/register.jsp") ||
				Objects.equals(request.getRequestURI(), "/login.jsp")
			) { %>
			<% } else if (session.getAttribute("loginId") == null) { %>
				<!-- 로그아웃 상태 -->
				<a href="${pageContext.request.contextPath}/login" class="bg-gray-100 hover:bg-[#FA5F29] hover:text-white px-4 py-2 shadow-sm rounded-xl">
					<span class="text-sm md:text-base">로그인</span>
				</a>
				<a href="${pageContext.request.contextPath}/register" class="bg-[#FF2C00] text-white hover:bg-[#FA5F29] px-4 py-2 shadow-sm rounded-xl">
					<span class="text-sm md:text-base">가입하기</span>
				</a>
			<% } else { %>
				<!-- 로그인 상태 -->
				<a href="${pageContext.request.contextPath}/notice" class="hover:bg-[#FA5F29] hover:text-white px-4 py-2 rounded-xl">
					<span class="text-sm md:text-base">공지사항</span>
				</a>
				<a href="${pageContext.request.contextPath}/recipe" class="hover:bg-[#FA5F29] hover:text-white px-4 py-2 rounded-xl">
					<span class="text-sm md:text-base">레시피</span>
				</a>
				<a href="${pageContext.request.contextPath}/calories" class="hover:bg-[#FA5F29] hover:text-white px-4 py-2 rounded-xl">
					<span class="text-sm md:text-base">칼로리</span>
				</a>
				<a href="${pageContext.request.contextPath}/my" class="hover:bg-[#FA5F29] hover:text-white px-4 py-2 rounded-xl">
					<span class="text-sm md:text-base">마이페이지</span>
				</a>
				<a href="${pageContext.request.contextPath}/logout.jsp" class="hover:bg-[#FA5F29] hover:text-white px-4 py-2 rounded-xl">
					<span class="text-sm md:text-base">로그아웃</span>
				</a>
			<% } %>
		</div>
	</div>
</div>
