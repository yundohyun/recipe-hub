package it.dohyun.recipe_hub.controller.admin;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import it.dohyun.recipe_hub.util.URLEncodeParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/member")
public class AdminMemberServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(AdminMemberServlet.class.getName());
  private final MemberDao dao = new MemberDao();

  // 관리자 여부 확인하는 메서드
  private MemberDto checkAdmin(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("loginId") == null) {
      // 로그인한 아이디가 없는 경우 로그인 페이지로
      resp.sendRedirect("/login");
      return null;
    }

    String loginId = (String) session.getAttribute("loginId");

    try {
      MemberDto me = dao.getMember(loginId);

      if (me == null || !me.isAdmin()) {
        logger.log(Level.WARNING, "관리자 권한이 없는 사용자의 접근입니다. id=" + loginId);
        resp.sendRedirect("/my");
        return null;
      }
      return me;
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "관리자 여부 확인 중 오류 발생", e);
      req.setAttribute("error", "관리자 여부 확인 중 오류가 발생했습니다.");
      req.getRequestDispatcher("member.jsp").forward(req, resp);
      return null;
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("loginId") == null) {
      resp.sendRedirect("/login");
      return;
    }

    String loginId = (String) session.getAttribute("loginId");

    try {
      // 관리자 여부 확인
      MemberDto me = dao.getMember(loginId);
      if (me == null || !me.isAdmin()) {
        resp.sendRedirect("/my");
        return;
      }

      // 회원 목록 조회
      ArrayList<MemberDto> members = dao.getMembers();
      req.setAttribute("members", members);
      // 조회된 내용은 members 변수에 담은 후, 포워드
      req.getRequestDispatcher("member.jsp").forward(req, resp);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "관리자 페이지 조회 중 오류가 발생했습니다.", e);
      req.setAttribute("error", "관리자 페이지 조회 중 오류가 발생했습니다.");
      req.getRequestDispatcher("member.jsp").forward(req, resp);
    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    resp.setContentType("application/json; charset=UTF-8");

    Map<String, String> body = URLEncodeParser.parseUrlEncodedBody(req);

    PrintWriter out = resp.getWriter();

    try {
      if (checkAdmin(req, resp) == null) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        out.write("{\"error\": \"관리자 권한 없음\"}");
        return;
      }

      String memberId = body.get("memberId");
      if (memberId == null || memberId.isBlank()) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"error\": \"memberId가 필요합니다\"}");
        return;
      }

      MemberDto target = dao.getMember(memberId);

      if (target == null) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.write("{\"error\": \"해당 회원이 존재하지 않습니다\"}");
        return;
      }

      String nickname = body.get("nickname");
      String avatar = body.get("avatar");
      String introduction = body.get("introduction");

      if (nickname != null && !nickname.isBlank()) target.setNickname(nickname);
      if (avatar != null) target.setAvatar(avatar);
      if (introduction != null) target.setIntroduce(introduction);

      dao.updateMember(target);

      resp.setStatus(HttpServletResponse.SC_OK);
      out.write("{\"message\": \"회원 정보가 수정되었습니다\"}");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "관리자 권한으로 수정 중 오류 발생", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"error\": \"수정 중 서버 오류 발생\"}");
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("application/json; charset=UTF-8");
    PrintWriter out = resp.getWriter();

    Map<String, String> body = URLEncodeParser.parseUrlEncodedBody(req);

    try {
      if (checkAdmin(req, resp) == null) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        out.write("{\"error\": \"관리자 권한 없음\"}");
        return;
      }

      String memberId = body.get("memberId");
      if (memberId == null || memberId.isBlank()) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"error\": \"memberId가 필요합니다\"}");
        return;
      }

      dao.deleteMember(memberId);

      resp.setStatus(HttpServletResponse.SC_OK);
      out.write("{\"message\": \"회원이 삭제되었습니다\"}");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "관리자 권한으로 삭제 중 오류 발생", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"error\": \"삭제 중 서버 오류 발생\"}");
    }
  }
}
