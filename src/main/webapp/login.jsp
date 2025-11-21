<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
	<head>
		<title>RecipeHub - 로그인</title>
		<%@ include file="/component/head.jsp" %>
	</head>
	<body class="min-h-screen">
		<div class="w-full min-h-screen flex flex-col items-center bg-gray-100">
			<%@ include file="/component/header.jsp" %>
			<div class="flex flex-col justify-center items-center w-full xl:w-295 flex-1 overflow-auto px-4 lg:px-8 xl:px-0">
				<div class="text-card-foreground bg-white flex items-center flex-col gap-8 rounded-2xl px-12 py-18">
					<div class="flex flex-col items-center gap-4">
						<span class="font-semibold text-4xl">로그인</span>
						<span class="text-lg text-gray-700">RecipeHub에 접속하세요</span>
					</div>
					<form class="flex flex-col gap-9" action="<c:url value="/login"/>" method="post">
						<div class="flex flex-col gap-4">
							<label class="flex flex-col gap-1" for="email">
								<span class="text-md">아이디</span>
								<input
									class="w-100 focus:border-none focus:outline-none text-md px-4 py-2 bg-[#F5F5F5] border-[#EFEFEF] rounded-xl"
									type="email" name="email" id="email"
									placeholder="me@example.com"
								>
							</label>
							<label class="flex flex-col gap-1" for="password">
								<span class="text-md">비밀번호</span>
								<input
									class="w-100 focus:border-none focus:outline-none text-md px-4 py-2 bg-[#F5F5F5] border-[#EFEFEF] rounded-xl"
									type="password" name="password" id="password"
									minlength="8" maxlength="32"
									placeholder="••••••••"
								>
								<% if (request.getAttribute("error") != null) { %>
									<span class="text-sm text-red-500">
										<%= request.getAttribute("error") %>
									</span>
								<% } %>
							</label>
						</div>
						<button type="submit" class="h-full bg-[#FF2C00] hover:bg-[#FA5F29] shadow-sm rounded-2xl py-2">
							<span class="text-base text-white text-lg">가입하기</span>
						</button>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>
