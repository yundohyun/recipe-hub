package it.dohyun.recipe_hub.controller.notice;

import it.dohyun.recipe_hub.dao.NoticeDao;
import it.dohyun.recipe_hub.model.NoticeDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/notice/view")
public class NoticeViewServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(NoticeViewServlet.class.getName());
  private final NoticeDao dao = new NoticeDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null || id.isBlank()) {
      req.setAttribute("error", "id가 필요합니다.");
      req.getRequestDispatcher("/notice/index.jsp").forward(req, resp);
      return;
    }

    try {
      NoticeDto notice = dao.getNotice(id);
      if (notice == null) {
        req.setAttribute("error", "해당 공지를 찾을 수 없습니다.");
        req.getRequestDispatcher("/notice/index.jsp").forward(req, resp);
        return;
      }
      req.setAttribute("notice", notice);
      req.getRequestDispatcher("/notice/view.jsp").forward(req, resp);
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "공지 조회 중 오류", e);
      req.setAttribute("error", "공지 조회 중 오류가 발생했습니다.");
      req.getRequestDispatcher("/notice/index.jsp").forward(req, resp);
    }
  }
}

