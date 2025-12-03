package it.dohyun.recipe_hub.controller.admin;

import it.dohyun.recipe_hub.dao.CaloriesDao;
import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.CaloriesDto;
import it.dohyun.recipe_hub.model.MemberDto;
import it.dohyun.recipe_hub.util.URLEncodeParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/calories")
public class AdminCaloriesServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(AdminCaloriesServlet.class.getName());
  private final CaloriesDao caloriesDao = new CaloriesDao();
  private final MemberDao memberDao = new MemberDao();

  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    // ID 기준으로 DB에서 일치하는 값을 삭제하는 API
    resp.setContentType("application/json; charset=utf-8");
    PrintWriter out = resp.getWriter();

    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("loginId") == null) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      out.write("{\"message\":\"로그인이 필요합니다.\"}");
      return;
    }

    String loginId = (String) session.getAttribute("loginId");

    try {
      MemberDto me = memberDao.getMember(loginId);
      if (me == null || !me.isAdmin()) {
        logger.log(Level.WARNING, "관리자 권한 없는 사용자의 접근 id=" + loginId);
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        out.write("{\"message\":\"관리자만 삭제할 수 있습니다.\"}");
        return;
      }
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "관리자 권한 확인 중 오류 발생", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"message\":\"관리자 권한 확인 중 서버 오류가 발생했습니다.\"}");
      return;
    }

    Map<String, String> params;

    try {
      params = URLEncodeParser.parseUrlEncodedBody(req);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "요청 본문 파싱 중 오류 발생", e);
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write("{\"message\":\"요청 데이터를 해석하는 중 오류가 발생했습니다.\"}");
      return;
    }

    String id = params.get("id");
    if (id == null || id.isBlank()) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write("{\"message\":\"id는 필수 값입니다.\"}");
      return;
    }

    try {
      CaloriesDto dto = caloriesDao.getCalories(id);
      if (dto == null) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.write("{\"message\":\"해당 칼로리 정보를 찾을 수 없습니다.\"}");
        return;
      }

      caloriesDao.deleteCalories(id);

      resp.setStatus(HttpServletResponse.SC_OK);
      out.write("{\"message\":\"칼로리 정보가 삭제되었습니다.\"}");
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "칼로리 삭제 중 오류 발생", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"message\":\"칼로리 삭제 중 서버 오류가 발생했습니다.\"}");
    }
  }
}
