<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
		<title>RecipeHub - Recipe</title>
		<%@ include file="/component/head.jsp" %>
    <style>
      @keyframes spin { to { transform: rotate(360deg); } }
      .spinner { animation: spin 0.8s linear infinite;  }
    </style>
  </head>
  <body class="min-h-screen">
		<div class="w-full min-h-screen flex flex-col items-center bg-gray-100">
			<%@ include file="/component/header.jsp" %>
			<div class="flex flex-col w-full xl:w-295 h-full px-4 lg:px-8 xl:px-0">
    		<div>
    		  <!-- 검색 영역 -->
    		  <div class="bg-white rounded-3xl border border-gray-100 p-8 mt-8 shadow-md">
    		    <div class="flex justify-between items-center mb-4 pb-4 border-b border-gray-200" >
    		      <h2 class="text-lg font-bold text-gray-900">칼로리 검색</h2>
    		      <span></span>
    		    </div>
    		    <div class="flex flex-row flex-wrap justify-center items-end gap-8 w-full">
    		      <div class="flex flex-col">
    		        <label for="foodName" class="text-sm font-medium text-gray-900 mb-2">
									식품명
								</label>
    		        <input
    		          type="text"
    		          id="foodName"
    		          placeholder="예: 사과, 계란, 쌀"
    		          class="px-4 py-2 border-2 border-gray-200 rounded-lg focus:outline-none focus:border-orange-600 focus:bg-white transition-all bg-gray-50"
    		        />
    		      </div>
    		      <div class="flex flex-col">
    		        <label
    		          for="numOfRows"
    		          class="text-sm font-medium text-gray-900 mb-2"
    		          >결과 수</label
    		        >
    		        <select
    		          id="numOfRows"
    		          class="px-4 py-2 border-2 border-gray-200 rounded-lg focus:outline-none focus:border-orange-600 focus:bg-white transition-all bg-gray-50"
    		        >
    		          <option value="10">10개</option>
    		          <option value="20">20개</option>
    		        </select>
    		      </div>
							<div class="flex flex-row justify-between items-center gap-4">
								<button id="searchBtn" class="bg-[#FF2C00] text-white hover:bg-[#FA5F29] px-6 py-3 shadow-sm rounded-xl">
									<span class="text-sm md:text-base">검색</span>
								</button>
								<button id="resetBtn" class="bg-gray-100 hover:bg-[#FA5F29] hover:text-white px-6 py-3 shadow-sm rounded-xl">
									<span class="text-sm md:text-base">초기화</span>
								</button>
							</div>
    		    </div>
    		  </div>

    		  <!-- 결과 영역 -->
    		  <div class="bg-white rounded-3xl border border-gray-100 p-8 mt-8 shadow-md">
    		    <div class="flex justify-between items-center mb-4 pb-4 border-b border-gray-200" >
    		      <h2 class="text-lg font-bold text-gray-900">칼로리 검색 결과</h2>
    		      <span
    		        id="resultCount"
    		        class="bg-gradient-to-r from-orange-50 to-orange-100 text-orange-600 font-bold px-4 py-2 rounded-lg border-2 border-orange-500">
								0개
							</span>
    		    </div>

    		    <div id="errorContainer"></div>
    		    <div
    		      id="loadingContainer"
    		      style="display: none"
    		      class="text-center py-8 text-gray-600"
    		    >
    		      <div
    		        class="inline-block w-6 h-6 border-4 border-gray-300 border-t-orange-600 rounded-full spinner mb-2"
    		      ></div>
    		      <p>로딩 중...</p>
    		    </div>

    		    <div id="resultsContainer">
    		      <div class="text-center py-8 text-gray-600">
    		        검색 결과가 없습니다. 위에서 검색을 시작하세요.
    		      </div>
    		    </div>

    		    <div
    		      id="paginationContainer"
    		      style="display: none"
    		      class="flex justify-center gap-2 mt-8"
    		    ></div>
    		  </div>
    		</div>
    		<!-- 상세정보 모달 -->
    		<div
    		  id="detailModal"
    		  style="display: none"
    		  class="fixed inset-0 bg-black bg-opacity-40 z-50 flex items-center justify-center"
    		>
    		  <div
    		    class="bg-white rounded-3xl p-8 max-w-2xl w-full mx-4 max-h-80vh overflow-y-auto shadow-2xl"
    		  >
    		    <div
    		      class="flex justify-between items-center mb-4 pb-4 border-b border-gray-200"
    		    >
    		      <h2 id="detailModalTitle" class="text-xl font-bold text-gray-900">
    		        식품 정보
    		      </h2>
    		      <button
    		        id="closeModalBtn"
    		        class="text-gray-600 hover:text-gray-900 text-2xl"
    		      >
    		        &times;
    		      </button>
    		    </div>
    		    <div id="detailModalBody"></div>
    		    <div class="flex justify-end gap-4 mt-8 pt-4 border-t border-gray-200">
    		      <button
    		        id="closeModalBtnBottom"
    		        class="px-6 py-2 bg-white text-orange-600 font-medium border-2 border-gray-200 rounded-lg hover:bg-gray-50"
    		      >
    		        닫기
    		      </button>
    		    </div>
    		  </div>
    		</div>
			</div>
		</div>

    <script>
      const API_BASE_URL = "/api/calories";
      const state = {
        currentPage: 1,
        totalItems: 0,
        foodName: "",
        currentResults: [],
      };

      const elements = {
        foodName: document.getElementById("foodName"),
        pageNo: document.getElementById("pageNo"),
        numOfRows: document.getElementById("numOfRows"),
        sortBy: document.getElementById("sortBy"),
        fromDate: document.getElementById("fromDate"),
        toDate: document.getElementById("toDate"),
        searchBtn: document.getElementById("searchBtn"),
        resetBtn: document.getElementById("resetBtn"),
        resultsContainer: document.getElementById("resultsContainer"),
        resultCount: document.getElementById("resultCount"),
        loadingContainer: document.getElementById("loadingContainer"),
        errorContainer: document.getElementById("errorContainer"),
        paginationContainer: document.getElementById("paginationContainer"),
        detailModal: document.getElementById("detailModal"),
        detailModalTitle: document.getElementById("detailModalTitle"),
        detailModalBody: document.getElementById("detailModalBody"),
        closeModalBtn: document.getElementById("closeModalBtn"),
        closeModalBtnBottom: document.getElementById("closeModalBtnBottom"),
      };

      elements.searchBtn.addEventListener("click", performSearch);
      elements.resetBtn.addEventListener("click", resetForm);
      elements.closeModalBtn.addEventListener("click", closeDetailModal);
      elements.closeModalBtnBottom.addEventListener("click", closeDetailModal);

      elements.foodName.addEventListener("keypress", function (e) {
        if (e.key === "Enter") performSearch();
      });

      elements.detailModal.addEventListener("click", function (e) {
        if (e.target === elements.detailModal) closeDetailModal();
      });

      async function performSearch() {
        const name = elements.foodName.value.trim();
        const page = 1;
        const limit = parseInt(elements.numOfRows.value) || 10;

        if (!name) {
          showError("식품명을 입력하세요.");
          return;
        }

        showLoading(true);
        elements.errorContainer.innerHTML = "";

        // 프론트에서는 POST로 동기화를 직접 호출하지 않고, 바로 GET으로 검색합니다.
        state.foodName = name;
        state.currentPage = page;

        const params = new URLSearchParams();
        params.append("name", name);
        params.append("page", page);
        params.append("limit", limit);

        try {
          const response = await fetch(API_BASE_URL + "?" + params.toString());
          if (!response.ok) throw new Error("검색 요청 실패");
          const data = await response.json();
          state.currentResults = data.items || [];
          state.totalItems = data.count || 0;
          displayResults(state.currentResults);
          elements.resultCount.textContent = state.totalItems + "개";
        } catch (error) {
          console.error("검색 오류:", error);
          showError("검색 중 오류가 발생했습니다.");
        } finally {
          showLoading(false);
        }
      }

      function displayResults(items) {
        if (items.length === 0) {
          elements.resultsContainer.innerHTML =
            '<div class="text-center py-8 text-gray-600">검색 결과가 없습니다.</div>';
          return;
        }

        let tableHTML =
          '<div class="overflow-x-auto"><table class="w-full text-sm">';
        tableHTML +=
          '<thead class="bg-gradient-to-r from-orange-100 to-orange-50">';
        tableHTML += "<tr>";
        tableHTML +=
          '<th class="px-4 py-3 text-left font-bold text-orange-600 border-b-2 border-orange-500">식품명</th>';
        tableHTML +=
          '<th class="px-4 py-3 text-left font-bold text-orange-600 border-b-2 border-orange-500">칼로리 (kcal)</th>';
        tableHTML +=
          '<th class="px-4 py-3 text-left font-bold text-orange-600 border-b-2 border-orange-500">단백질 (g)</th>';
        tableHTML +=
          '<th class="px-4 py-3 text-left font-bold text-orange-600 border-b-2 border-orange-500">탄수화물 (g)</th>';
        tableHTML +=
          '<th class="px-4 py-3 text-left font-bold text-orange-600 border-b-2 border-orange-500">지질 (g)</th>';
        tableHTML +=
          '<th class="px-4 py-3 text-left font-bold text-orange-600 border-b-2 border-orange-500">작업</th>';
        tableHTML += "</tr>";
        tableHTML += "</thead>";
        tableHTML += "<tbody>";

        for (let i = 0; i < items.length; i++) {
          const item = items[i];
          tableHTML +=
            '<tr class="border-b border-gray-100 hover:bg-gray-50 transition-colors">';
          tableHTML +=
            '<td class="px-4 py-3">' + escapeHtml(item.name || "-") + "</td>";
          tableHTML +=
            '<td class="px-4 py-3 font-bold text-orange-600">' +
            formatNumber(item.calories) +
            "</td>";
          tableHTML +=
            '<td class="px-4 py-3">' + formatNumber(item.protein) + "</td>";
          tableHTML +=
            '<td class="px-4 py-3">' +
            formatNumber(item.carbohydrates) +
            "</td>";
          tableHTML +=
            '<td class="px-4 py-3">' + formatNumber(item.fat) + "</td>";
          tableHTML += '<td class="px-4 py-3">';
          tableHTML +=
            '<button class="px-3 py-1 bg-white text-orange-600 font-medium border-2 border-gray-200 rounded-lg hover:bg-gray-50 text-xs" onclick="showDetail(' +
            i +
            ')">상세보기</button>';
          tableHTML += "</td>";
          tableHTML += "</tr>";
        }

        tableHTML += "</tbody>";
        tableHTML += "</table></div>";

        elements.resultsContainer.innerHTML = tableHTML;
      }

      function showDetail(index) {
        const item = state.currentResults[index];
        if (!item) return;

        elements.detailModalTitle.textContent = item.name || "식품 정보";

        let nutritionHTML =
          '<div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">';

        const nutritionItems = [
          { label: "칼로리", value: formatNumber(item.calories), unit: "kcal" },
          { label: "단백질", value: formatNumber(item.protein), unit: "g" },
          {
            label: "탄수화물",
            value: formatNumber(item.carbohydrates),
            unit: "g",
          },
          { label: "지질 (지방)", value: formatNumber(item.fat), unit: "g" },
          { label: "나트륨", value: formatNumber(item.sodium), unit: "mg" },
          { label: "칼슘", value: formatNumber(item.calcium), unit: "mg" },
        ];

        for (let i = 0; i < nutritionItems.length; i++) {
          const nut = nutritionItems[i];
          nutritionHTML +=
            '<div class="bg-gradient-to-br from-orange-50 to-orange-100 p-4 rounded-lg border-2 border-orange-500">';
          nutritionHTML +=
            '<div class="text-xs font-bold text-gray-600 uppercase mb-2 tracking-wider">' +
            nut.label +
            "</div>";
          nutritionHTML +=
            '<div class="text-2xl font-bold text-orange-600">' +
            nut.value +
            '<span class="text-xs text-gray-600 font-normal"> ' +
            nut.unit +
            "</span></div>";
          nutritionHTML += "</div>";
        }

        nutritionHTML += "</div>";

        if (item.id) {
          nutritionHTML +=
            '<div class="text-xs text-gray-600 mt-4">ID: ' +
            escapeHtml(item.id) +
            "</div>";
        }

        elements.detailModalBody.innerHTML = nutritionHTML;
        elements.detailModal.style.display = "flex";
      }

      function closeDetailModal() {
        elements.detailModal.style.display = "none";
      }

      function resetForm() {
        elements.foodName.value = "";
        elements.pageNo.value = "1";
        elements.numOfRows.value = "10";
        elements.sortBy.value = "";
        elements.fromDate.value = "";
        elements.toDate.value = "";
        elements.errorContainer.innerHTML = "";
        elements.resultsContainer.innerHTML =
          '<div class="text-center py-8 text-gray-600">검색 결과가 없습니다. 위에서 검색을 시작하세요.</div>';
        elements.resultCount.textContent = "0개";
        elements.foodName.focus();
      }

      function showLoading(show) {
        elements.loadingContainer.style.display = show ? "flex" : "none";
      }

      function showError(message) {
        elements.errorContainer.innerHTML =
          '<div class="bg-red-50 text-red-700 px-4 py-3 rounded-lg border-2 border-red-300 mb-4">⚠️ ' +
          escapeHtml(message) +
          "</div>";
      }

      function showSuccess(message) {
        elements.errorContainer.innerHTML =
          '<div class="bg-gradient-to-br from-orange-50 to-orange-100 text-orange-600 px-4 py-3 rounded-lg border-2 border-orange-200 mb-4">✓ ' +
          escapeHtml(message) +
          "</div>";
      }

      function formatNumber(value) {
        if (value === null || value === undefined || value === "") return "-";
        const num = parseFloat(value);
        return isNaN(num) ? "-" : num.toFixed(2);
      }

      function escapeHtml(text) {
        const div = document.createElement("div");
        div.textContent = text;
        return div.innerHTML;
      }

      elements.foodName.focus();
    </script>
  </body>
</html>
