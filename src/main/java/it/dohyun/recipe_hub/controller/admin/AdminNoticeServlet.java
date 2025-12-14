package it.dohyun.recipe_hub.controller.admin;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.dao.NoticeDao;
import it.dohyun.recipe_hub.model.MemberDto;
import it.dohyun.recipe_hub.model.NoticeDto;
import it.dohyun.recipe_hub.util.URLEncodeParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/notice")
public class AdminNoticeServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(AdminNoticeServlet.class.getName());
  private final MemberDao memberDao = new MemberDao();
  private final NoticeDao noticeDao = new NoticeDao();

  private MemberDto checkAdmin(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("loginId") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return null;
    }
    String loginId = (String) session.getAttribute("loginId");
    try {
      MemberDto me = memberDao.getMember(loginId);
      // Debug logging to help identify why admin check may fail
      if (me == null) {
        logger.log(Level.WARNING, "checkAdmin: no member found for loginId=" + loginId);
      } else {
        logger.log(Level.INFO, "checkAdmin: member found id=" + me.getId() + ", admin=" + me.isAdmin());
      }

      if (me == null || !me.isAdmin()) {
        logger.log(Level.WARNING, "관리자 권한이 없는 사용자의 접근입니다. id=" + loginId + " admin=" + (me == null ? "null" : me.isAdmin()));
        resp.sendRedirect(req.getContextPath() + "/my");
        return null;
      }
      return me;
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "관리자 여부 확인 중 오류 발생", e);
      req.setAttribute("error", "관리자 여부 확인 중 오류가 발생했습니다.");
      req.getRequestDispatcher("/admin/notice.jsp").forward(req, resp);
      return null;
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (checkAdmin(req, resp) == null) return;

    try {
      ArrayList<NoticeDto> list = noticeDao.getNotices();
      req.setAttribute("notices", list);
      req.getRequestDispatcher("/admin/notice.jsp").forward(req, resp);
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "공지 목록 조회 오류", e);
      req.setAttribute("error", "공지 목록을 불러오는 중 오류가 발생했습니다.");
      req.getRequestDispatcher("/admin/notice.jsp").forward(req, resp);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    resp.setContentType("application/json; charset=UTF-8");

    PrintWriter out = resp.getWriter();

    try {
      if (checkAdmin(req, resp) == null) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        out.write("{\"error\": \"관리자 권한 없음\"}");
        return;
      }

      String title = req.getParameter("title");
      String content = req.getParameter("content");

      if (title == null || title.isBlank() || content == null || content.isBlank()) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"error\": \"title/content가 필요합니다\"}");
        return;
      }

      NoticeDto dto = new NoticeDto();
      dto.setId(UUID.randomUUID().toString());
      dto.setTitle(title);
      dto.setContent(content);

      noticeDao.createNotice(dto);

      resp.setStatus(HttpServletResponse.SC_OK);
      out.write("{\"message\": \"공지가 생성되었습니다\"}");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "공지 생성 중 오류", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"error\": \"서버 오류 발생\"}");
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

      String id = body.get("id");
      if (id == null || id.isBlank()) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"error\": \"id가 필요합니다\"}");
        return;
      }

      NoticeDto target = noticeDao.getNotice(id);
      if (target == null) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.write("{\"error\": \"해당 공지가 없습니다\"}");
        return;
      }

      String title = body.get("title");
      String content = body.get("content");

      if (title != null && !title.isBlank()) target.setTitle(title);
      if (content != null) target.setContent(content);

      noticeDao.updateNotice(target);

      resp.setStatus(HttpServletResponse.SC_OK);
      out.write("{\"message\": \"공지가 수정되었습니다\"}");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "공지 수정 중 오류", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"error\": \"서버 오류 발생\"}");
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("application/json; charset=UTF-8");
    Map<String, String> body = URLEncodeParser.parseUrlEncodedBody(req);
    PrintWriter out = resp.getWriter();

    try {
      if (checkAdmin(req, resp) == null) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        out.write("{\"error\": \"관리자 권한 없음\"}");
        return;
      }

      String id = body.get("id");
      if (id == null || id.isBlank()) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"error\": \"id가 필요합니다\"}");
        return;
      }

      noticeDao.deleteNotice(id);
      resp.setStatus(HttpServletResponse.SC_OK);
      out.write("{\"message\": \"공지가 삭제되었습니다\"}");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "공지 삭제 중 오류", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.write("{\"error\": \"서버 오류 발생\"}");
    }
  }
}
