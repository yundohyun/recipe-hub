package it.dohyun.recipe_hub.controller.recipe;

import it.dohyun.recipe_hub.dao.*;
import it.dohyun.recipe_hub.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/recipe/view")
public class RecipeViewServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(RecipeViewServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		String id = req.getParameter("id");
		if (id == null || id.isBlank()) {
			resp.sendRedirect(req.getContextPath() + "/");
			return;
		}
		
		RecipeDao recipeDao = new RecipeDao();
		RecipeIngredientDao ingredientDao = new RecipeIngredientDao();
		RecipeContentDao contentDao = new RecipeContentDao();
		RecipeContentImageDao contentImageDao = new RecipeContentImageDao();
		RecipeCaloriesDao recipeCaloriesDao = new RecipeCaloriesDao();
		MemberDao memberDao = new MemberDao();
		RecipeLikeDao recipeLikeDao = new RecipeLikeDao();
		
		try {
			logger.log(java.util.logging.Level.FINE, "RecipeViewServlet called for id=" + id + " session=" + req.getSession(false));
			RecipeDto recipe = recipeDao.getRecipe(id);
			if (recipe == null) {
				resp.sendRedirect(req.getContextPath() + "/");
				return;
			}
			
			// Increment view count once per session to avoid duplicates on refresh
			HttpSession session = req.getSession();
			@SuppressWarnings("unchecked")
			Set<String> viewed = (Set<String>) session.getAttribute("viewedRecipes");
			if (viewed == null) {
				viewed = new HashSet<>();
				session.setAttribute("viewedRecipes", viewed);
			}
			if (!viewed.contains(id)) {
				try {
					// increment stored view count and update DTO
					recipeDao.addViewCount(id);
					Integer vc = recipe.getViewCount();
					recipe.setViewCount((vc == null ? 0 : vc) + 1);
				} catch (SQLException | ClassNotFoundException ex) {
					logger.log(Level.WARNING, "view count increment failed for id=" + id, ex);
				}
				viewed.add(id);
			}
			
			List<RecipeIngredientDto> ingredients = ingredientDao.getRecipeIngredientsByRecipeId(id);
			if (ingredients == null) ingredients = new ArrayList<>();
			List<RecipeContentDto> contents = contentDao.getRecipeContents(id);
			if (contents == null) contents = new ArrayList<>();
			
			// map content id -> list of image urls
			Map<String, List<String>> contentImages = new HashMap<>();
			for (RecipeContentDto cd : contents) {
				List<RecipeContentImageDto> imgs = contentImageDao.getRecipeContentImages(cd.getId());
				List<String> imageUrls = new ArrayList<>();
				for (RecipeContentImageDto rci : imgs) {
					if (rci.getImageUrl() != null && !rci.getImageUrl().isBlank())
						imageUrls.add(rci.getImageUrl());
				}
				contentImages.put(cd.getId(), imageUrls);
			}
			
			RecipeCaloriesDto rc = null;
			try {
				rc = recipeCaloriesDao.getRecipeCalories(id);
			} catch (Exception ex) {
				logger.log(Level.FINE, "No single recipe-calories mapping for id=" + id, ex);
			}
			MemberDto author = null;
			try {
				author = memberDao.getMember(recipe.getMemberId());
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Author lookup failed for memberId=" + recipe.getMemberId(), ex);
			}
			
			// 좋아요 수 조회
			int likeCount = 0;
			try {
				likeCount = recipeLikeDao.countLikesByRecipeId(id);
			} catch (SQLException | ClassNotFoundException ex) {
				logger.log(Level.WARNING, "좋아요 수 조회 중 오류", ex);
			}
			
			// whether current logged-in user liked this recipe
			boolean liked = false;
			try {
				String loginId = null;
				if (req.getSession(false) != null) loginId = (String) req.getSession().getAttribute("loginId");
				if (loginId != null) {
					liked = recipeLikeDao.getRecipeLikeByRecipeAndMember(id, loginId) != null;
				}
			} catch (Exception ex) {
				logger.log(Level.FINE, "좋아요 상태 조회 실패", ex);
			}
			
			// load multiple calories linked to this recipe
			java.util.List<RecipeCaloriesDto> rcList = null;
			try {
				rcList = recipeCaloriesDao.getRecipeCaloriesList(id);
			} catch (Exception ex) {
				logger.log(Level.FINE, "No recipe-calories list for id=" + id, ex);
			}
			java.util.List<CaloriesDto> caloriesList = new java.util.ArrayList<>();
			CaloriesDao caloriesDao = new CaloriesDao();
			if (rcList != null) {
				for (RecipeCaloriesDto mapping : rcList) {
					try {
						if (mapping == null || mapping.getCaloriesId() == null) continue;
						CaloriesDto cd = caloriesDao.getCalories(mapping.getCaloriesId());
						if (cd != null) caloriesList.add(cd);
					} catch (Exception ex) {
						logger.log(Level.WARNING, "연결된 칼로리 조회 실패: " + (mapping == null ? "null" : mapping.getCaloriesId()), ex);
					}
				}
			}
			
			// if single mapping exists, resolve it to CaloriesDto for legacy 'recipeCalories' attribute
			CaloriesDto primaryCalories = null;
			try {
				if (rc != null && rc.getCaloriesId() != null) {
					primaryCalories = new CaloriesDao().getCalories(rc.getCaloriesId());
				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Primary calories lookup failed for id=" + (rc == null ? "null" : rc.getCaloriesId()), ex);
			}
			
			req.setAttribute("recipe", recipe);
			req.setAttribute("ingredients", ingredients);
			req.setAttribute("contents", contents);
			req.setAttribute("contentImages", contentImages);
			req.setAttribute("recipeCalories", primaryCalories);
			req.setAttribute("recipeCaloriesList", caloriesList);
			req.setAttribute("author", author);
			req.setAttribute("likeCount", likeCount);
			req.setAttribute("liked", liked);
			
			req.getRequestDispatcher("/recipe/view.jsp").forward(req, resp);
			
		} catch (Exception e) {
			// catch any exception to avoid JSP 500 and log details for debugging
			logger.log(Level.SEVERE, "레시피 조회 중 오류", e);
			resp.sendRedirect(req.getContextPath() + "/");
		}
	}
}
