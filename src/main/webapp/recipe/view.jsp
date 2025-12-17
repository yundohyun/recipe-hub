<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
  <head>
    <title><c:out value="${recipe.title}"/> - RecipeHub</title>
    <%@ include file="/component/head.jsp" %>
    <style>
      /* like button visuals: outline by default, filled when parent has .liked */
      .like-icon { width: 28px; height: 28px; display: block; }
      .like-icon path { fill: none; stroke: currentColor; stroke-width: 1.8; stroke-linecap: round; stroke-linejoin: round; }
      button[aria-label="like-recipe"].liked { color: #ef4444; }
      button[aria-label="like-recipe"].liked .like-icon path { fill: #ef4444; stroke: none; }

      /* simple modal for image enlarge */
      .img-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.75); display:none; align-items:center; justify-content:center; z-index:9999; }
      .img-modal-overlay.open { display:flex; }
      .img-modal-overlay img { max-width: calc(100% - 40px); max-height: calc(100% - 40px); border-radius:8px; box-shadow:0 10px 30px rgba(0,0,0,0.6); }
    </style>
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
                   <img src="${recipe.thumbnail}" alt="${recipe.title}" class="w-full h-full object-cover rounded-xl" data-enlargeable="true" style="cursor:zoom-in;" />
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
                  <c:choose>
                    <c:when test="${not empty sessionScope.loginId}">
                      <button class="flex flex-col items-center gap-1 p-3 rounded-lg hover:bg-muted transition-colors bg-white border ${liked ? 'liked' : ''}" aria-label="like-recipe">
                        <!-- unified heart SVG: CSS will color/ fill when button has .liked -->
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="w-7 h-7 like-icon" aria-hidden="true">
                          <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z" />
                        </svg>
                        <span class="text-sm font-semibold text-foreground like-count"><c:out value="${likeCount}"/></span>
                      </button>
<%--                      <a href="${pageContext.request.contextPath}/recipe/edit?id=${recipe.id}" class="text-sm text-primary hover:underline">수정</a>--%>
                    </c:when>
                    <c:otherwise>
<%--                      <a href="${pageContext.request.contextPath}/login.jsp" class="text-sm text-muted-foreground px-3 py-2 border rounded">로그인 후 이용</a>--%>
                    </c:otherwise>
                  </c:choose>
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
                                <c:otherwise>-</c:otherwise>
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
                                <img src="${img}" alt="step image" class="w-36 h-36 object-cover rounded" data-enlargeable="true" style="cursor:zoom-in;" />
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
                  <h2 class="text-lg font-semibold text-foreground mb-3">영양 정보 (1인분)</h2>

                  <!-- Compact nutrition list: show up to 5 related nutrition items in small cards -->
                  <div class="grid grid-cols-1 gap-2">
                    <c:choose>
                      <c:when test="${not empty recipeCaloriesList}">
                        <div class="grid grid-cols-1 gap-2">
                          <c:forEach items="${recipeCaloriesList}" var="cal" varStatus="st">
                            <c:if test="${st.index < 5}">
                              <div class="bg-white rounded-lg border border-border shadow-sm p-2 flex items-center justify-between text-xs">
                                <div>
                                  <div class="font-medium text-sm"><c:out value="${cal.name}"/></div>
                                  <div class="text-muted-foreground text-xxs">1회: <c:out value="${cal.serve}"/></div>
                                </div>
                                <div class="text-right">
                                  <div class="font-semibold text-sm"><c:out value="${cal.calories}"/> kcal</div>
                                  <div class="text-muted-foreground text-xxs">P <c:out value="${cal.protein}"/>g • F <c:out value="${cal.fat}"/>g</div>
                                </div>
                              </div>
                            </c:if>
                          </c:forEach>
                        </div>
                      </c:when>
                      <c:when test="${not empty recipeCalories}">
                        <div class="bg-white rounded-lg border border-border shadow-sm p-2 text-sm">
                          <div class="flex items-center justify-between">
                            <div>
                              <div class="font-medium"><c:out value="${recipeCalories.name}"/></div>
                              <div class="text-muted-foreground text-xs">1회: <c:out value="${recipeCalories.serve}"/></div>
                            </div>
                            <div class="text-right">
                              <div class="font-semibold"><c:out value="${recipeCalories.calories}"/> kcal</div>
                              <div class="text-muted-foreground text-xs">P <c:out value="${recipeCalories.protein}"/>g • F <c:out value="${recipeCalories.fat}"/>g</div>
                            </div>
                          </div>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <div class="bg-white rounded-lg border border-border shadow-sm p-3 text-sm text-center text-muted-foreground">등록된 연관 칼로리 정보가 없습니다.</div>
                      </c:otherwise>
                    </c:choose>
                  </div>

                  <!-- DEBUG PANEL: show helpful server-side values when ?debug=true -->
                  <c:if test="${param.debug == 'true'}">
                    <div class="mt-6 bg-yellow-50 border border-yellow-200 rounded p-4 text-sm text-slate-800">
                      <h4 class="font-semibold mb-2">DEBUG: 서버 전달 데이터</h4>
                      <pre style="white-space:pre-wrap;">Recipe: id=${recipe.id}
title=${recipe.title}
category=${recipe.category}
serve=${recipe.serve}
duration=${recipe.duration}
thumbnail=${recipe.thumbnail}
description=${recipe.description}</pre>

                      <h5 class="mt-2 font-medium">Primary calories (recipeCalories)</h5>
                      <c:choose>
                        <c:when test="${not empty recipeCalories}">
                          <pre>name=${recipeCalories.name}
calories=${recipeCalories.calories}
protein=${recipeCalories.protein}
fat=${recipeCalories.fat}
carbohydrates=${recipeCalories.carbohydrates}
serve=${recipeCalories.serve}</pre>
                        </c:when>
                        <c:otherwise>
                          <pre>primary calories: null</pre>
                        </c:otherwise>
                      </c:choose>

                      <h5 class="mt-2 font-medium">Calories list (recipeCaloriesList) count: ${fn:length(recipeCaloriesList)}</h5>
                      <c:forEach items="${recipeCaloriesList}" var="cidx" varStatus="s">
                        <pre>[${s.index}] name=${cidx.name} | kcal=${cidx.calories} | protein=${cidx.protein} | fat=${cidx.fat} | carbs=${cidx.carbohydrates}</pre>
                      </c:forEach>

                      <h5 class="mt-2 font-medium">Ingredients (${fn:length(ingredients)})</h5>
                      <c:forEach items="${ingredients}" var="ing" varStatus="is">
                        <pre>[${is.index}] ${ing.ingredient} — ${ing.amount}</pre>
                      </c:forEach>

                      <h5 class="mt-2 font-medium">Contents (${fn:length(contents)})</h5>
                      <c:forEach items="${contents}" var="ct" varStatus="cs">
                        <pre>[${cs.index}] step=${ct.step} — ${ct.content}</pre>
                      </c:forEach>
                    </div>
                  </c:if>

                 </div>
               </aside>
              <script>
                (function(){
                  const likeBtn = document.querySelector('button[aria-label="like-recipe"]');
                  const likeCountEl = likeBtn ? likeBtn.querySelector('.like-count') : null;
                  if (!likeBtn) return;
                  const recipeId = '${recipe.id}';
                  likeBtn.addEventListener('click', async function(e){
                    e.preventDefault();
                    try {
                      const res = await fetch('${pageContext.request.contextPath}/recipe/like', {
                        method: 'POST',
                        headers: {'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'},
                        body: 'recipeId=' + encodeURIComponent(recipeId)
                      });
                      if (!res.ok) {
                        if (res.status === 401) { alert('로그인 후 좋아요를 눌러주세요.'); return; }
                        throw new Error('like failed');
                      }
                      const json = await res.json();
                      if (likeCountEl) likeCountEl.textContent = json.likeCount;
                      // toggle filled style
                      if (json.liked) likeBtn.classList.add('liked'); else likeBtn.classList.remove('liked');
                    } catch(err) { console.error(err); alert('좋아요 처리 중 오류가 발생했습니다.'); }
                  });
                })();
              </script>

              <!-- Image enlarge modal + behavior -->
              <div id="img-modal" class="img-modal-overlay" role="dialog" aria-hidden="true">
                <img id="img-modal-img" src="" alt="enlarged image" />
              </div>

              <script>
                (function(){
                  const modal = document.getElementById('img-modal');
                  const modalImg = document.getElementById('img-modal-img');
                  function openModal(src, alt){
                    modalImg.src = src;
                    if(alt) modalImg.alt = alt;
                    modal.classList.add('open');
                    modal.setAttribute('aria-hidden','false');
                    document.body.style.overflow = 'hidden';
                  }
                  function closeModal(){
                    modal.classList.remove('open');
                    modal.setAttribute('aria-hidden','true');
                    modalImg.src = '';
                    document.body.style.overflow = '';
                  }
                  // bind to all images marked data-enlargeable
                  document.querySelectorAll('img[data-enlargeable]') .forEach(function(img){
                    img.style.cursor = img.style.cursor || 'zoom-in';
                    img.addEventListener('click', function(){ openModal(img.src, img.alt || 'image'); });
                  });
                  // close on overlay click or ESC
                  modal.addEventListener('click', function(e){ if(e.target === modal || e.target === modalImg) closeModal(); });
                  document.addEventListener('keydown', function(e){ if(e.key === 'Escape') closeModal(); });
                })();
              </script>
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
