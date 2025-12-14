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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/notice")
public class NoticeListServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(NoticeListServlet.class.getName());
  private final NoticeDao dao = new NoticeDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      ArrayList<NoticeDto> list = dao.getNotices();
      req.setAttribute("list", list);
      req.getRequestDispatcher("/notice/index.jsp").forward(req, resp);
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "공지 목록 조회 중 오류", e);
      req.setAttribute("error", "공지 목록을 불러오는 중 오류가 발생했습니다.");
      req.getRequestDispatcher("/notice/index.jsp").forward(req, resp);
    }
  }
}

