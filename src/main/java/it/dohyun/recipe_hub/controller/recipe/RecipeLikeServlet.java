package it.dohyun.recipe_hub.controller.recipe;

import it.dohyun.recipe_hub.dao.RecipeLikeDao;
import it.dohyun.recipe_hub.model.RecipeLikeDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/recipe/like")
public class RecipeLikeServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(RecipeLikeServlet.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String recipeId = req.getParameter("recipeId");
		String loginId = null;
		if (req.getSession(false) != null) loginId = (String) req.getSession().getAttribute("loginId");
		
		resp.setContentType("application/json;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		
		if (loginId == null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			out.print("{\"error\":\"login_required\"}");
			return;
		}
		if (recipeId == null || recipeId.isBlank()) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("{\"error\":\"missing_recipeId\"}");
			return;
		}
		
		RecipeLikeDao likeDao = new RecipeLikeDao();
		try {
			// check existing
			var existing = likeDao.getRecipeLikeByRecipeAndMember(recipeId, loginId);
			if (existing == null) {
				RecipeLikeDto dto = new RecipeLikeDto();
				dto.setRecipeId(recipeId);
				dto.setMemberId(loginId);
				likeDao.setRecipeLike(dto);
			} else {
				likeDao.deleteRecipeLikeByRecipeAndMember(recipeId, loginId);
			}
			int newCount = likeDao.countLikesByRecipeId(recipeId);
			boolean liked = (likeDao.getRecipeLikeByRecipeAndMember(recipeId, loginId) != null);
			out.print(String.format("{\"likeCount\":%d,\"liked\":%s}", newCount, liked ? "true" : "false"));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Like toggle failed", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\":\"server_error\"}");
		}
	}
}

