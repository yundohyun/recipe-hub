<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
	<head>
		<title>RecipeHub</title>
		<%@ include file="/component/head.jsp" %>
	</head>
	<body class="min-h-screen">
		<div class="w-full min-h-screen flex flex-col items-center bg-gray-100">
			<%@ include file="/component/header.jsp" %>
			<div class="flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0">
				<section class="flex flex-col items-center w-full gap-y-8 py-16">
					<span class="text-4xl md:text-5xl text-center font-semibold text-balance">
						균형잡힌 식단과 함께하는 <span class="text-[#FF2C00]">더 건강한 하루</span>
					</span>
					<span class="text-center text-xs md:text-xl font-light text-gray-400 text-balance">
						칼로리 검색을 통해 나의 하루 식습관을 돌아보고<br />
						음식에 관련된 다양한 경험과 저칼로리 레시피를 서로 나눠보세요.
					</span>
					<div class="flex flex-row w-fit gap-x-4">
						<a href="<c:url value="/calories"/>" class="flex flex-row gap-x-2 items-center bg-[#FF2C00] text-white hover:bg-[#FA5F29] px-6 py-3 shadow-sm rounded-xl">
							<img src="<c:url value="/assets/calories/white.svg"/>" class="w-4 h-4 md:w-6 md:h-6">
							<span class="text-sm md:text-lg font-light">칼로리 검색하기</span>
						</a>
						<a href="<c:url value="/recipe"/>" class="flex flex-row gap-x-2 items-center bg-white text-black hover:bg-gray-50 px-6 py-3 shadow-sm rounded-xl">
							<img src="<c:url value="/assets/recipe/black.svg"/>" class="w-4 h-4 md:w-6 md:h-6" alt="레시피 아이콘">
							<span class="text-sm md:text-lg font-light">레시피 둘러보기</span>
						</a>
					</div>
				</section>
				<section class="bg-[#FFE8E0] px-8 py-16 rounded-2xl">
					<div class="w-full flex flex-col items-center gap-y-4">
						<span class="font-semibold text-3xl text-center text-balance">
							<span class="text-[#FF2C00]">RecipeHub</span>의 기능
						</span>
						<div class="grid grid-cols-1 sm:grid-cols-3 xl:grid-cols-3 justify-between w-full gap-4 py-6">
							<div class="bg-white shadow-sm rounded-2xl flex flex-col p-8 gap-y-4">
								<img src="<c:url value="/assets/recipe/primary.svg"/>" class="w-12 h-12">
								<span class="font-medium text-xl">레시피 공유</span>
								<span class="text-gray-400 text-pretty">나만의 특별한 저칼로리 레시피를 공유해 보세요.</span>
							</div>
							<div class="bg-white shadow-sm rounded-2xl flex flex-col p-8 gap-y-4">
								<img src="<c:url value="/assets/calories/primary.svg"/>" class="w-12 h-12">
								<span class="font-medium text-xl">칼로리 검색</span>
								<span class="text-gray-400 text-pretty">정보가 궁금한 음식을 검색해 칼로리와 영양 정보를 확인하세요.</span>
							</div>
							<div class="bg-white shadow-sm rounded-2xl flex flex-col p-8 gap-y-4">
								<img src="<c:url value="/assets/announcement/primary.svg"/>" class="w-12 h-12">
								<span class="font-medium text-xl">공지사항</span>
								<span class="text-gray-400 text-pretty">서비스 업데이트와 이벤트 소식을 빠르게 확인하세요.</span>
							</div>
						</div>
					</div>
				</section>
				<section class="flex flex-col items-center w-full gap-y-4 py-8">
					<div class="bg-[#FFE8E0] border border-[#FA5F29] flex flex-col items-center w-full gap-y-8 px-4 py-12 rounded-2xl">
						<span class="text-3xl font-semibold">
							지금 바로 시작하세요
						</span>
						<span class="text-sm text-center text-gray-500">
							RecipeHub에 가입하고<br class="block md:hidden" />
							칼로리 관리와 건강한 식습관을 함께 만들어가요!
						</span>
						<a href="<c:url value="/register"/>" class="bg-[#FF2C00] text-white hover:bg-[#FA5F29] px-6 py-3 shadow-sm rounded-xl">
							<span>회원가입 하기</span>
						</a>
					</div>
				</section>
			</div>
		</div>
	</body>
</html>
