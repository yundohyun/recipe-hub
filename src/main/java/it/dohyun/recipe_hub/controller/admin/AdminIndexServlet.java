package it.dohyun.recipe_hub.controller.admin;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.dao.RecipeDao;
import it.dohyun.recipe_hub.dao.NoticeDao;
import it.dohyun.recipe_hub.dao.CaloriesDao;
import it.dohyun.recipe_hub.model.MemberDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin")
public class AdminIndexServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(AdminIndexServlet.class.getName());
  private final MemberDao memberDao = new MemberDao();
  private final RecipeDao recipeDao = new RecipeDao();
  private final NoticeDao noticeDao = new NoticeDao();
  private final CaloriesDao caloriesDao = new CaloriesDao();

  private MemberDto checkAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("loginId") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return null;
    }
    String loginId = (String) session.getAttribute("loginId");
    try {
      MemberDto me = memberDao.getMember(loginId);
      if (me == null || !me.isAdmin()) {
        resp.sendRedirect(req.getContextPath() + "/my");
        return null;
      }
      return me;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "관리자 여부 확인 중 오류", e);
      req.setAttribute("error", "관리자 여부 확인 중 오류가 발생했습니다.");
      return null;
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (checkAdmin(req, resp) == null) return;

    try {
      ArrayList<MemberDto> members = memberDao.getMembers();
      int totalMembers = members != null ? members.size() : 0;

      int totalRecipes = recipeDao.countRecipes("", null);

      ArrayList<?> notices = noticeDao.getNotices();
      int totalNotices = notices != null ? notices.size() : 0;

      int totalCalories = caloriesDao.countCalories();

      req.setAttribute("totalMembers", totalMembers);
      req.setAttribute("totalRecipes", totalRecipes);
      req.setAttribute("totalNotices", totalNotices);
      req.setAttribute("totalCalories", totalCalories);

      req.getRequestDispatcher("/admin/index.jsp").forward(req, resp);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "관리자 대시보드 로드 중 오류", e);
      req.setAttribute("error", "관리자 대시보드 로드 중 오류가 발생했습니다.");
      req.getRequestDispatcher("/admin/index.jsp").forward(req, resp);
    }
  }
}
