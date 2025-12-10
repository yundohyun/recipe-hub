package it.dohyun.recipe_hub.controller.calories;

import it.dohyun.recipe_hub.api.CaloriesAPI;
import it.dohyun.recipe_hub.common.types.FindOption;
import it.dohyun.recipe_hub.common.types.SortEnum;
import it.dohyun.recipe_hub.dao.CaloriesDao;
import it.dohyun.recipe_hub.model.CaloriesDto;
import it.dohyun.recipe_hub.util.PropertyUtil;
import it.dohyun.recipe_hub.util.URLEncodeParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/api/calories")
public class CaloriesAPIServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(CaloriesAPIServlet.class.getName());
  private final CaloriesDao dao = new CaloriesDao();
  private CaloriesAPI service;

  @Override
  public void init() {
    // API 키 준비
    PropertyUtil prop = new PropertyUtil("api.properties", List.of("calories.api.key"));
    String apiKey = prop.getProperty("calories.api.key");

    this.service = new CaloriesAPI(apiKey, dao);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json; charset=utf-8");
    PrintWriter out = resp.getWriter();

    String id = req.getParameter("id");

    String name = req.getParameter("name");
    String pageParam = req.getParameter("page");
    String limitParam = req.getParameter("limit");
    String sortParam = req.getParameter("sort");
    String fromParam = req.getParameter("from");
    String toParam = req.getParameter("to");

    try {
      // id가 있다면, 하나의 건수 상세 조회
      if (id != null && !id.isBlank()) {
        CaloriesDto dto = dao.getCalories(id);

        if (dto == null) {
          resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"message\":\"해당 칼로리 정보를 찾을 수 없습니다.\"}");
          return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        out.write(dto.toJSON().toString());
        return;
      }

      FindOption option = new FindOption();

      try {
        if (pageParam != null && !pageParam.isBlank()) {
          option.setPage(Integer.parseInt(pageParam));
        }
        if (limitParam != null && !limitParam.isBlank()) {
          option.setLimit(Integer.parseInt(limitParam));
        }
      } catch (NumberFormatException ignore) {
        // 잘못된 숫자 형식일 시, 옵션 없이 조회
      }

      if (sortParam != null && !sortParam.isBlank()) {
        try {
          option.setSort(SortEnum.valueOf(sortParam.toUpperCase()));
        } catch (IllegalArgumentException ignore) {
          // 잘못된 값이면 건너뛰기
        }
      }

      try {
        if (fromParam != null && !fromParam.isBlank()) {
          option.setFrom(LocalDateTime.parse(fromParam));
        }
        if (toParam != null && !toParam.isBlank()) {
          option.setTo(LocalDateTime.parse(toParam));
        }
      } catch (Exception ignore) {
        // 실패 시 기간 필터 건너뛰기
      }

      String keyword = (name == null) ? "" : name;
      // 요청한 페이지와 개수(기본값)
      int reqPage = 1;
      int reqLimit = 10;
      try {
        if (pageParam != null && !pageParam.isBlank()) reqPage = Integer.parseInt(pageParam);
      } catch (NumberFormatException ignore) {
      }
      try {
        if (limitParam != null && !limitParam.isBlank()) reqLimit = Integer.parseInt(limitParam);
      } catch (NumberFormatException ignore) {
      }
      ArrayList<CaloriesDto> list = dao.searchCalories(keyword, option);

      // DB에 내용이 없으면, 외부 API에서 동기화한 후 동기적으로 재조회 (기존 동작)
      // DB에 내용이 없거나(또는 1페이지 요청에서 DB 결과가 요청한 개수보다 적으면) 외부 API에서 동기화한 후 재조회
      if (list.isEmpty() || (reqPage == 1 && list.size() < reqLimit)) {
        int syncPageNo;
        int syncNumOfRows;
        // 동기화할 페이지/건수는 요청한 값으로 맞춤
        syncPageNo = reqPage;
        syncNumOfRows = reqLimit;

        try {
          service.syncPageToDb(syncPageNo, syncNumOfRows, keyword);
          // 동기화 후 재조회
          list = dao.searchCalories(keyword, option);
        } catch (IOException e) {
          logger.log(Level.SEVERE, "외부 칼로리 API 호출 중 오류 발생", e);
          resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
          out.write("{\"message\":\"외부 칼로리 API 호출 중 서버 오류가 발생했습니다.\"}");
          return;
        } catch (SQLException | ClassNotFoundException e) {
          logger.log(Level.SEVERE, "칼로리 DB저장/조회 중 오류 발생", e);
          resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          out.write("{\"message\":\"칼로리 조회 중 서버 오류가 발생했습니다.\"}");
          return;
        }
      }

      JSONArray items = new JSONArray();
      for (CaloriesDto c : list) {
        items.put(c.toJSON());
      }

      // 리스트(목록) 조회 후, 값을 담는 공간
      JSONObject result = new JSONObject();
      result.put("items", items);
      result.put("count", list.size());

      resp.setStatus(HttpServletResponse.SC_OK);
      out.write(result.toString());

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "칼로리 조회 중 오류가 발생했습니다.", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"message\":\"칼로리 조회 중 서버 오류가 발생했습니다.\"}");
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // 식품 영양 API 값을 가져와, DB에 저장하는 API
    resp.setContentType("application/json; charset=utf-8");
    PrintWriter out = resp.getWriter();

    Map<String, String> params;
    try {
      params = URLEncodeParser.parseUrlEncodedBody(req);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "요청 본문 파싱 중 오류 발생", e);
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write("{\"message\":\"요청 데이터를 해석하는 중 오류가 발생했습니다.\"}");
      return;
    }

    String foodName = params.get("name");
    String pageParams = params.get("pageNo");
    String rowsParams = params.get("numOfRows");

    int pageNo = 1;
    int numOfRows = 10;

    try {
      if (pageParams != null && !pageParams.isBlank()) {
        pageNo = Integer.parseInt(pageParams);
      }

      if (rowsParams != null && !rowsParams.isBlank()) {
        numOfRows = Integer.parseInt(rowsParams);
      }
    } catch (NumberFormatException ignore) {

    }

    try {
      // API에서 데이터 가져오기
      List<CaloriesDto> saved = service.syncPageToDb(pageNo, numOfRows, foodName);

      JSONArray items = new JSONArray();
      for (CaloriesDto c : saved) {
        items.put(c.toJSON());
      }

      JSONObject result = new JSONObject();
      result.put("items", items);
      result.put("count", saved.size());
      result.put("message", "칼로리 정보가 동기화되었습니다.");

      resp.setStatus(HttpServletResponse.SC_OK);
      out.write(result.toString());

    } catch (IOException e) {
      logger.log(Level.SEVERE, "외부 칼로리 API 호출 중 오류 발생", e);
      resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
      out.write("{\"message\":\"외부 칼로리 API 호출 중 서버 오류가 발생했습니다.\"}");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "칼로리 DB저장 중 오류 발생", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"message\":\"칼로리 동기화 중 서버 오류가 발생했습니다.\"}");
    }
  }
}
