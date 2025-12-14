<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title><c:out value="${recipe.title}"/> - RecipeHub</title>
    <%@ include file="/component/head.jsp" %>
  </head>
  <body class="min-h-screen bg-gray-50 text-slate-900">
    <div class="w-full min-h-screen flex flex-col items-center">
      <%@ include file="/component/header.jsp" %>

      <c:choose>
        <c:when test="${not empty recipe}">
          <!-- widened container for desktop -->
					<div class="max-h-6xl flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0">

            <!-- Hero -->
            <div class="mt-12 h-72 md:h-96 lg:h-[28rem] bg-gradient-to-br from-primary/20 to-secondary/20 rounded-xl flex items-center justify-center text-9xl mb-8 border border-border overflow-hidden relative">
               <c:choose>
                 <c:when test="${not empty recipe.thumbnail}">
                   <img src="${recipe.thumbnail}" alt="${recipe.title}" class="w-full h-full object-cover rounded-xl" />
                 </c:when>
                 <c:otherwise>
                   <div class="w-full h-full bg-gray-100 flex items-center justify-center">
                     <svg class="w-24 h-24 text-gray-300" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                       <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V7" />
                       <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7a4 4 0 118 0" />
                     </svg>
                   </div>
                 </c:otherwise>
               </c:choose>
             </div>

            <!-- Title / summary / actions -->
            <div class="mb-8">
              <div class="flex items-start justify-between mb-4">
                <div>
                  <h1 class="text-4xl font-extrabold text-foreground mb-2"><c:out value="${recipe.title}"/></h1>
                  <p class="text-muted-foreground text-lg max-w-2xl">
                    <c:choose>
                      <c:when test="${not empty recipe.description}"><c:out value="${recipe.description}"/></c:when>
                      <c:otherwise>간단하고 맛있는 레시피입니다.</c:otherwise>
                    </c:choose>
                  </p>
                  <!-- Category badge -->
                  <c:if test="${not empty recipe.category}">
                    <div class="mt-3">
                      <span class="inline-flex whitespace-nowrap items-center gap-1 px-3 py-1 rounded-full bg-muted text-sm font-medium">
                        <c:choose>
                          <c:when test="${recipe.category == 'etc'}">카테고리: 기타</c:when>
                          <c:when test="${recipe.category == 'egg'}">카테고리: 계란요리</c:when>
                          <c:when test="${recipe.category == 'street'}">카테고리: 분식</c:when>
                          <c:when test="${recipe.category == 'soup'}">카테고리: 국&amp;탕</c:when>
                          <c:when test="${recipe.category == 'rice'}">카테고리: 밥요리</c:when>
                          <c:when test="${recipe.category == 'pasta'}">카테고리: 파스타</c:when>
                          <c:when test="${recipe.category == 'grill'}">카테고리: 구이</c:when>
                          <c:otherwise>카테고리: <c:out value="${recipe.category}"/></c:otherwise>
                        </c:choose>
                      </span>
                    </div>
                  </c:if>
                </div>

                <div class="flex flex-col items-center gap-3">
                  <button class="flex flex-col items-center gap-1 p-3 rounded-lg hover:bg-muted transition-colors bg-white border">
                    <!-- heart icon -->
                    <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" class="w-7 h-7 text-muted-foreground">
                      <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"></path>
                    </svg>
                    <span class="text-sm font-semibold text-foreground"><c:out value="${likeCount}"/></span>
                  </button>
                  <a href="${pageContext.request.contextPath}/recipe/edit?id=${recipe.id}" class="text-sm text-primary hover:underline">수정</a>
                </div>
              </div>

              <!-- Author + small meta -->
              <div class="flex items-center gap-4 mb-6 pb-6 border-b border-border">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 rounded-full bg-muted flex items-center justify-center text-2xl overflow-hidden">
                    <c:choose>
                      <c:when test="${not empty author and not empty author.avatar}">
                        <img src="${author.avatar}" alt="${author.nickname}" class="w-12 h-12 object-cover rounded-full" />
                      </c:when>
                      <c:otherwise>👨‍🍳</c:otherwise>
                    </c:choose>
                  </div>
                  <div>
                    <p class="font-semibold text-foreground"><c:out value="${author.nickname}"/></p>
                    <!-- rating removed intentionally per spec -->
                  </div>
                </div>
              </div>

              <!-- stats grid -->
              <div class="grid grid-cols-4 gap-4 mb-6">
                <div class="bg-muted p-4 rounded-lg text-center">
                  <div class="flex items-center justify-center gap-2 mb-2 text-primary">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                  </div>
                  <p class="font-semibold text-foreground text-lg"><c:out value="${recipe.duration}"/>분</p>
                  <p class="text-sm text-muted-foreground">조리시간</p>
                </div>

                <div class="bg-muted p-4 rounded-lg text-center">
                  <div class="flex items-center justify-center gap-2 mb-2 text-primary">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg>
                  </div>
                  <p class="font-semibold text-foreground text-lg"><c:out value="${recipe.serve}"/>인분</p>
                  <p class="text-sm text-muted-foreground">인분</p>
                </div>

                <div class="bg-muted p-4 rounded-lg text-center">
                  <div class="flex items-center justify-center gap-2 mb-2 text-primary">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"></path></svg>
                  </div>
                  <p class="font-semibold text-foreground text-lg">
                    <c:choose>
                      <c:when test="${not empty recipeCalories}"><c:out value="${recipeCalories.calories}"/></c:when>
                      <c:otherwise>—</c:otherwise>
                    </c:choose>
                  </p>
                  <p class="text-sm text-muted-foreground">kcal</p>
                </div>

                <div class="bg-muted p-4 rounded-lg text-center">
                  <div class="flex items-center justify-center gap-2 mb-2">
                    <c:choose>
                      <c:when test="${recipe.difficulty == 'hard'}">
                        <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2s4 4 4 8c0 4-3 6-3 11-4-2-5-6-5-9 0-4 4-10 4-10z" fill="#ef4444"/></svg>
                      </c:when>
                      <c:when test="${recipe.difficulty == 'medium'}">
                        <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M13 2L3 14h7l-1 8 10-12h-7l1-8z" fill="#f59e0b"/></svg>
                      </c:when>
                      <c:otherwise>
                        <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2C7 2 2 7 2 12c0 5 4 10 10 10 5 0 10-5 10-10 0-5-6-10-10-10z" fill="#16a34a"/></svg>
                      </c:otherwise>
                    </c:choose>
                  </div>
                  <p class="font-semibold text-foreground text-lg"><c:out value="${not empty recipe.difficulty ? recipe.difficulty : '쉬움'}"/></p>
                  <p class="text-sm text-muted-foreground">난이도</p>
                </div>
              </div>
            </div>

            <!-- Main content: ingredients / steps + nutrition (responsive 12-col layout) -->
            <div class="mb-16 grid grid-cols-1 lg:grid-cols-12 gap-8">

              <!-- Left: ingredients + steps (span 8 on large) -->
              <div class="lg:col-span-8 space-y-10">

                <!-- Ingredients -->
                <section>
                  <h2 class="text-2xl font-semibold text-foreground mb-4">재료</h2>
                  <div class="w-full">
                    <div class="bg-white rounded-xl border border-border shadow-sm p-6">
                      <div class="grid grid-cols-2 gap-4">
                        <c:forEach items="${ingredients}" var="ing">
                          <div class="flex items-center justify-between py-2 border-b last:border-0">
                            <span class="text-foreground">${ing.ingredient}</span>
                            <span class="font-medium text-primary text-sm">
                              <c:choose>
                                <c:when test="${not empty ing.amount}"><c:out value="${ing.amount}"/></c:when>
                                <c:otherwise>&nbsp;</c:otherwise>
                              </c:choose>
                            </span>
                          </div>
                        </c:forEach>
                      </div>
                    </div>
                  </div>
                </section>

                <!-- Steps -->
                <section>
                  <h2 class="text-2xl font-semibold text-foreground mb-4">조리 과정</h2>
                  <div class="space-y-4">
                    <c:forEach items="${contents}" var="step">
                      <article class="bg-white rounded-xl border border-border shadow-sm p-6 flex gap-6 items-start">
                        <div class="flex-shrink-0 w-12 h-12 bg-primary text-primary-foreground rounded-full flex items-center justify-center font-bold text-lg">${step.step}</div>
                        <div class="flex-1">
                          <p class="text-foreground leading-relaxed text-base"><c:out value="${step.content}"/></p>
                          <c:if test="${not empty contentImages[step.id]}">
                            <div class="flex gap-3 mt-4 flex-wrap">
                              <c:forEach items="${contentImages[step.id]}" var="img">
                                <img src="${img}" alt="step image" class="w-36 h-36 object-cover rounded" />
                              </c:forEach>
                            </div>
                          </c:if>
                        </div>
                      </article>
                    </c:forEach>
                  </div>
                </section>

              </div>

              <!-- Right: nutrition card (span 4) -->
              <aside class="lg:col-span-4">
                <div class="lg:sticky lg:top-24">
                  <h2 class="text-xl font-semibold text-foreground mb-4">영양 정보 (1인분)</h2>
                  <div class="bg-white rounded-xl border border-border shadow-sm p-6 space-y-3">
                    <div class="flex justify-between items-center pb-3 border-b"><span class="text-muted-foreground">칼로리</span><span class="font-bold text-foreground text-lg"><c:choose><c:when test="${not empty recipeCalories}"><c:out value="${recipeCalories.calories}"/></c:when><c:otherwise>—</c:otherwise></c:choose></span></div>
                    <div class="flex justify-between items-center pb-3 border-b"><span class="text-muted-foreground">단백질</span><span class="font-bold text-foreground"><c:choose><c:when test="${not empty recipeCalories}"><c:out value="${recipeCalories.protein}"/></c:when><c:otherwise>—</c:otherwise></c:choose></span></div>
                    <div class="flex justify-between items-center pb-3 border-b"><span class="text-muted-foreground">탄수화물</span><span class="font-bold text-foreground"><c:choose><c:when test="${not empty recipeCalories}"><c:out value="${recipeCalories.carbohydrates}"/></c:when><c:otherwise>—</c:otherwise></c:choose></span></div>
                    <div class="flex justify-between items-center pb-3 border-b"><span class="text-muted-foreground">지방</span><span class="font-bold text-foreground"><c:choose><c:when test="${not empty recipeCalories}"><c:out value="${recipeCalories.fat}"/></c:when><c:otherwise>—</c:otherwise></c:choose></span></div>
                    <div class="flex justify-between items-center"><span class="text-muted-foreground">식이섬유</span><span class="font-bold text-foreground"><c:choose><c:when test="${not empty recipeCalories}"><c:out value="${recipeCalories.fiber}"/></c:when><c:otherwise>—</c:otherwise></c:choose></span></div>
                  </div>

                  <div class="mt-6">
                    <button class="w-full bg-primary text-primary-foreground rounded-md py-3 font-medium">레시피 저장</button>
                  </div>
                </div>
              </aside>

            </div>

           </div>
         </c:when>
         <c:otherwise>
           <div class="max-w-4xl w-full mx-auto px-4 py-8">
             <div class="bg-white rounded-xl p-8 shadow-md text-center">
               <h2 class="text-2xl font-bold mb-4">레시피를 찾을 수 없습니다.</h2>
               <p class="text-gray-600">요청하신 레시피가 존재하지 않거나 서버에서 데이터를 가져오지 못했습니다.</p>
               <a href="${pageContext.request.contextPath}/recipe" class="mt-4 inline-block bg-primary text-primary-foreground px-4 py-2 rounded">레시피 목록으로</a>
             </div>
           </div>
         </c:otherwise>
       </c:choose>

     </div>
   </body>
 </html>
