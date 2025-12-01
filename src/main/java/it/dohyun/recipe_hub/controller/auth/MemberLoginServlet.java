package it.dohyun.recipe_hub.controller.auth;

import it.dohyun.recipe_hub.dao.MemberDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
public class MemberLoginServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(MemberLoginServlet.class.getName());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // 로그인 페이지로 포워드 (추후 경로 설정)
    req.getRequestDispatcher("login.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("utf-8");
    String email = req.getParameter("email");
    String password = req.getParameter("password");

    MemberDao dao = new MemberDao();
    String memberId;

    try {
      memberId = dao.checkLogin(email, password);
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "로그인 처리 중 예외 발생", e);
      req.setAttribute("error", "로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      // 오류 발생하면 다시 로그인 페이지로 포워드
      req.getRequestDispatcher("login.jsp").forward(req, resp);
      return;
    }

    if (memberId != null) {
      // 로그인 성공 시 id(난수값)을 세션 영역에 저장
      HttpSession session = req.getSession();
      session.setAttribute("loginId", memberId);

      // Session 영역의 loginId 값을 통해, 로그인 비로그인 구분
      resp.sendRedirect(req.getContextPath() + "/");
    } else {
      req.setAttribute("error", "이메일(아이디) 또는 비밀번호가 올바르지 않습니다.");
      // 에러 발생 시 로그인 페이지로 포워드 (추후 경로 설정)
      req.getRequestDispatcher("login.jsp").forward(req, resp);
    }
  }
}
