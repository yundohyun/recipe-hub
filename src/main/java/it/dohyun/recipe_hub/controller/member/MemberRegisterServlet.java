package it.dohyun.recipe_hub.controller.member;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/register")
public class MemberRegisterServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(MemberRegisterServlet.class.getName());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // 회원가입 페이지로 포워드 (추후 경로 설정)
    req.getRequestDispatcher("register.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("utf-8");

    String email = req.getParameter("email");
    String password = req.getParameter("password");
    String nickname = req.getParameter("nickname");

    MemberDto dto = new MemberDto();
    dto.setEmail(email);
    dto.setPassword(password);
    dto.setNickname(nickname);

    boolean hasError = false;

    // 입력하지 않은 여러 값을 표시
    // 이메일 유효성, 중복 에러 -> emailError
    // 비밀번호 길이 검사 에러 -> passwordError
    // 닉네임 유효성 및 중복 에러 -> nicknameError

    if (email == null || email.isBlank()) {
      // 이메일만 해당되는 에러메시지
      req.setAttribute("emailError", "이메일은 필수 입력칸입니다.");
      hasError = true;
    } else if (!email.matches("^[A-Za-z0-9]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
      req.setAttribute("emailError", "이메일 형식이 올바르지 않습니다.");
      hasError = true;
    }

    if (password == null || password.length() < 8 || password.length() > 32) {
      // 비밀번호만 해당되는 에러메시지
      req.setAttribute("passwordError", "비밀번호는 8자 이상, 32자 이하여야 합니다.");
      hasError = true;
    }

    if (nickname == null || nickname.isBlank()) {
      // 닉네임만 해당되는 에러메시지
      req.setAttribute("nicknameError", "닉네임은 필수 입력칸입니다.");
      hasError = true;
    }

    if (hasError) {
      // 자신이 어떤 값을 입력했는지 확인하기 위한 입력 기본값 세팅
      req.setAttribute("emailValue", email);
      req.setAttribute("nicknameValue", nickname);
      req.getRequestDispatcher("register.jsp").forward(req, resp);
      return;
    }

    MemberDao dao = new MemberDao();

    try {

      if (dao.checkEmailExist(email)) {
        // 이메일만 해당되는 에러메시지 (중복검증)
        req.setAttribute("emailError", "이미 사용 중인 이메일입니다.");
        hasError = true;
      }

      if (dao.checkNicknameExist(nickname)) {
        // 닉네임만 해당되는 에러메시지 (중복검증)
        req.setAttribute("nicknameError", "이미 사용 중인 닉네임입니다.");
        hasError = true;
      }

      if (hasError) {
        // 자신이 어떤 값을 입력했는지 확인하기 위한 입력 기본값 세팅
        req.setAttribute("emailValue", email);
        req.setAttribute("nicknameValue", nickname);
        req.getRequestDispatcher("register.jsp").forward(req, resp);
        return;
      }

      dao.setMember(dto);
      // 회원가입 성공 시 /login Servlet으로 리다이렉트
      resp.sendRedirect(req.getContextPath() + "/login");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "회원가입 처리 중 예외 발생", e);
      req.setAttribute("error", "회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.");
      // 실패 시 에러메시지 출력 후, 회원가입 페이지로 다시 포워드
      req.getRequestDispatcher("register.jsp").forward(req, resp);
    }
  }
}
