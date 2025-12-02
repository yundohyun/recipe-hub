package it.dohyun.recipe_hub.util;

import it.dohyun.recipe_hub.dao.CaloriesDao;
import it.dohyun.recipe_hub.model.CaloriesDto;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class CaloriesService {
	private static final String BASE_URL =
		"https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02";

	private final String apiKey;
	private final CaloriesDao caloriesDao;

	public CaloriesService(String apiKey, CaloriesDao caloriesDao) {
		this.apiKey = apiKey;
		this.caloriesDao = caloriesDao;
	}

	// 요청 URL 생성
	private String buildUrl(int pageNo, int numOfRows, String foodName) {
		StringBuilder sb = new StringBuilder(BASE_URL);
		sb.append("?serviceKey=").append(URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
		sb.append("&pageNo=").append(pageNo);
		sb.append("&numOfRows=").append(numOfRows);
		sb.append("&type=json");

		// 식품명 검색 조건
		if (foodName != null && !foodName.isBlank()) {
			sb.append("&FOOD_NM_KR=")
				.append(URLEncoder.encode(foodName, StandardCharsets.UTF_8));
		}
		return sb.toString();
	}

	// HTTP GET 호출 및 JSON 문자열 받기
	private String callApi(int pageNo, int numOfRows, String foodName)
			throws IOException {
		String urlStr = buildUrl(pageNo, numOfRows, foodName);
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);

		int status = conn.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			throw new IOException("API 호출 실패: HTTP " + status);
		}

		try (BufferedReader br = new BufferedReader(
			new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} finally {
			conn.disconnect();
		}
	}

	public List<CaloriesDto> fetchFromApi(int pageNo, int numOfRows, String foodName)
			throws IOException {
		String json = callApi(pageNo, numOfRows, foodName);
		JSONObject root = new JSONObject(json);

		JSONObject body = root.getJSONObject("body");
		JSONArray items = body.getJSONArray("items");

		List<CaloriesDto> result = new ArrayList<>();
		if (items == null) {
			return result;
		}

		for (int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			CaloriesDto dto = toDto(item);
			result.add(dto);
		}
		return result;
	}

	private CaloriesDto toDto(JSONObject item) {
		CaloriesDto dto = new CaloriesDto();

		// 식품명
		String name = item.optString("FOOD_NM_KR", "").trim();
		dto.setName(name.isEmpty() ? null : name);

		// 제공량
		String serving = item.optString("SERVING_SIZE", "").trim();
		dto.setServe(parseServe(serving));

		// 칼로리
		double kcal = item.optDouble("AMT_NUM1", 0.0);
		dto.setCalories((int) Math.round(kcal));

		dto.setProtein(item.optDouble("AMT_NUM3", 0.0));
		dto.setFat(item.optDouble("AMT_NUM4", 0.0));
		dto.setCarbohydrates(item.optDouble("AMT_NUM6", 0.0));

		return dto;
	}

	private int parseServe(String servingSize) {
		if (servingSize == null) return 0;
		String digits = servingSize.replaceAll("[^0-9]", "");
		if (digits.isEmpty()) return 0;
		try {
			return Integer.parseInt(digits);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

//	이름과 칼로리가 같은 데이터가 이미 존재할 경우, 저장 건너뛰기
//	public List<CaloriesDto> syncPageToDb(int pageNo, int numOfRows, String foodName)
//			throws IOException, SQLException , ClassNotFoundException {
//		List<CaloriesDto> fromApi = fetchFromApi(pageNo, numOfRows, foodName);
//		List<CaloriesDto> saved = new ArrayList<>();
//
//		for (CaloriesDto dto : fromApi) {
//
//			이름, 칼로리가 비어있으면 건너뛰기
//			if (dto.getName() == null || dto.getName().isBlank()) {
//				continue;
//			}
//
//			이름 + 칼로리를 키로 이미 일치하면 insert 하지 않음
//			if (caloriesDao.existsByNameANdCalories(dto.getName(), dto.getCalories()))
//			caloriesDao.createCalories(dto);
//		 }
//		return fromApi;
//	}

	public CaloriesDto getCalories(String id)
			throws ClassNotFoundException, SQLException {
		return caloriesDao.getCalories(id);
	}

	public void deleteCalories(String id)
			throws ClassNotFoundException, SQLException {
		caloriesDao.deleteCalories(id);
	}
}




