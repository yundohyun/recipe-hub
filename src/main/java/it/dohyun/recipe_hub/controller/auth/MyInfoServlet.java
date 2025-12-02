package it.dohyun.recipe_hub.controller.auth;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import it.dohyun.recipe_hub.util.URLEncodeParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

@WebServlet("/my")
public class MyInfoServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(MyInfoServlet.class.getName());
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
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);

    // 혹시나 로그인이 되지 않았다면, 로그인 페이지로 이동
    if (session == null || session.getAttribute("loginId") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    String id = (String) session.getAttribute("loginId");

    try {
      MemberDto dto = dao.getMember(id);
      if (dto == null) {
        session.invalidate();
        resp.sendRedirect(req.getContextPath() + "/login");
        return;
      }

      // member에 저장된 dto 값을 통해 접근 가능
      req.setAttribute("member", dto);
      req.getRequestDispatcher("my.jsp").forward(req, resp);

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "회원 정보 불러오는 중 에러 발생", e);
      req.setAttribute("error", "회원 정보를 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      req.getRequestDispatcher("index.jsp").forward(req, resp); // 에러 발생 시 홈으로 이동
    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("loginId") == null) {
      this.sendResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
      return;
    }

    String id = (String) session.getAttribute("loginId");

    // Map 클래스 params
    Map<String, String> params;
    try {
      params = URLEncodeParser.parseUrlEncodedBody(req);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "요청 Body 파싱 중 오류 발생", e);
      this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "요청 데이터를 해석하는 중 오류가 발생했습니다.");
      return;
    }

    String email = params.get("email");
    String nickname = params.get("nickname");
    String avatar = params.get("avatar");
    String introduce = params.get("introduce");

    try {
      MemberDto dto = dao.getMember(id);
      if (dto == null) {
        // 세션은 있으나, 모종의 이유로 회원 정보를 찾을 수 없는 경우
        // 바로 JSON으로 에러 메시지 반환
        session.invalidate();
        this.sendResponse(
            res, HttpServletResponse.SC_UNAUTHORIZED, "회원 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
        return;
      }

      // 이메일 변경 시 유효성 검사
      if (email == null
          || email.isBlank()
          || !email.matches("^[A-Za-z0-9]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        return;
      }

      // 닉네임 변경 시 유효성 검사
      if (nickname == null || nickname.isBlank()) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "닉네임은 필수 입력칸입니다.");
        return;
      }

      // 사용자가 입력 한 이메일이 본인의 원래 이메일과 다르면서, 고유한 값이 아닐 시 에러
      if (dao.checkEmailExist(email) && !Objects.requireNonNull(email).equals(dto.getEmail())) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이미 사용 중인 이메일입니다.");
        return;
      }

      // 사용자가 입력 한 닉네임이 본인의 원래 닉네임과 다르면서, 고유한 값이 아닐 시 에러
      if (dao.checkNicknameExist(nickname)
          && !Objects.requireNonNull(nickname).equals(dto.getNickname())) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이미 사용 중인 닉네임입니다.");
        return;
      }

      // 값 업데이트
      dto.setEmail(email);
      dto.setNickname(nickname);
      dto.setAvatar(avatar);
      dto.setIntroduce(introduce);

      dao.updateMember(dto);

      this.sendResponse(res, HttpServletResponse.SC_OK, "회원 정보가 성공적으로 수정되었습니다.");

    } catch (Exception e) {
      logger.log(Level.SEVERE, "회원 정보 수정 중 에러 발생", e);
      this.sendResponse(
          res,
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "회원 정보 수정 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
    HttpSession session = req.getSession(false);

    // 혹시나 로그인이 되지 않았다면 401 출력
    if (session == null || session.getAttribute("loginId") == null) {
      this.sendResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
      return;
    }

    String id = (String) session.getAttribute("loginId");

    try {
      dao.deleteMember(id);
      session.invalidate();

      this.sendResponse(res, HttpServletResponse.SC_OK, "회원 탈퇴가 완료되었습니다.");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "회원 탈퇴 중 에러 발생", e);
      this.sendResponse(
          res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "회원 탈퇴 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
  }
}
