package it.dohyun.recipe_hub.controller.recipe;

import it.dohyun.recipe_hub.dao.RecipeDao;
import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.RecipeDto;
import it.dohyun.recipe_hub.model.MemberDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/recipe")
public class RecipeListServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(RecipeListServlet.class.getName());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String q = req.getParameter("q");
    String pageStr = req.getParameter("page");
    int page = 1;
    int limit = 10;
    try {
      if (pageStr != null) page = Integer.parseInt(pageStr);
    } catch (NumberFormatException ignored) {
    }

    RecipeDao recipeDao = new RecipeDao();
    MemberDao memberDao = new MemberDao();

    try {
      int total = recipeDao.countRecipes(q);
      List<RecipeDto> list = recipeDao.searchRecipes(q, page, limit);

      Map<String, MemberDto> authors = new HashMap<>();
      for (RecipeDto r : list) {
        MemberDto m = memberDao.getMember(r.getMemberId());
        if (m != null) authors.put(r.getMemberId(), m);
      }

      req.setAttribute("list", list);
      req.setAttribute("authors", authors);
      req.setAttribute("total", total);
      req.setAttribute("page", page);
      req.setAttribute("limit", limit);
      req.setAttribute("q", q == null ? "" : q);

      req.getRequestDispatcher("/recipe/index.jsp").forward(req, resp);
    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "레시피 목록 조회 중 오류", e);
      resp.sendRedirect(req.getContextPath() + "/");
    }
  }
}

