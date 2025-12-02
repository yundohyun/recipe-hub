package it.dohyun.recipe_hub.controller.auth;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import it.dohyun.recipe_hub.util.URLEncodeParser;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

// 비밀번호 관리 서블릿
@WebServlet("/password")
public class ChangePasswordServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(ChangePasswordServlet.class.getName());
  private final MemberDao dao = new MemberDao();

  private void sendResponse(HttpServletResponse res, int status, String message)
      throws IOException {
    res.setContentType("application/json; charset=UTF-8");
    res.setStatus(status);
    JSONObject result = new JSONObject();
    result.put("message", message);
    res.getWriter().write(result.toString());
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    HttpSession session = req.getSession(false);

    // 로그인 여부 확인
    if (session == null || session.getAttribute("loginId") == null) {
      this.sendResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
      return;
    }

    String id = (String) session.getAttribute("loginId");

    Map<String, String> params;
    try {
      params = URLEncodeParser.parseUrlEncodedBody(req);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "요청 본문 파싱 중 오류 발생");
      this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "요청 데이터를 해석하는 중 오류가 발생했습니다.");
      return;
    }

    String currentPassword = params.get("currentPassword");
    String newPassword = params.get("newPassword");

    // 새로운 비밀번호 및 비밀번호 확인 유효성 검사
    if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 32) {
      this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "비밀번호는 8자 이상, 32자 이하여야 합니다.");
      return;
    }

    try {
      MemberDto dto = dao.getMember(id);

      // 세션은 있으나, 모종의 이유로 회원 정보를 찾을 수 없는 경우
      if (dto == null) {
        session.invalidate();
        this.sendResponse(
            res, HttpServletResponse.SC_UNAUTHORIZED, "회원 정보를 찾을 수 없습니다. 다시 로그인 해주세요.");
        return;
      }

      String hashedPassword = dto.getPassword();

      // 해시된 현재 비밀번호를 가져와, 일치하는지 검사
      if (!BCrypt.checkpw(currentPassword, hashedPassword)) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        return;
      }

      // 비밀번호 변경
      dao.updateMemberPassword(id, newPassword);

      // 성공 시
      this.sendResponse(res, HttpServletResponse.SC_OK, "비밀번호가 성공적으로 변경되었습니다.");
    } catch (SQLException | ClassNotFoundException e) {
      // 비밀번호 변경 중 오류 발생
      logger.log(Level.SEVERE, "비밀번호 변경 중 오류가 발생했습니다.");
      this.sendResponse(
          res,
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "비밀번호 변경 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
  }
}
