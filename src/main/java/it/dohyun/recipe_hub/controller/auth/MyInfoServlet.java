package it.dohyun.recipe_hub.controller.auth;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/my")
public class MyInfoServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(MyInfoServlet.class.getName());
	private final MemberDao dao = new MemberDao();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		// 혹시나 로그인이 되지 않았다면, 로그인 페이지로 이동
		if (session == null || session.getAttribute("loginId") == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		
		String id = (String) session.getAttribute("loginId");
		
		try {
			MemberDto dto = dao.getMember(id);
			if (dto == null) {
				session.invalidate();
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}
			
			// load liked recipes for this user
			java.util.List<it.dohyun.recipe_hub.model.RecipeDto> likedRecipes = new java.util.ArrayList<>();
			try {
				it.dohyun.recipe_hub.dao.RecipeLikeDao likeDao = new it.dohyun.recipe_hub.dao.RecipeLikeDao();
				java.util.List<String> likedIds = likeDao.getRecipeIdsByMemberId(id);
				it.dohyun.recipe_hub.dao.RecipeDao recipeDao = new it.dohyun.recipe_hub.dao.RecipeDao();
				for (String rid : likedIds) {
					try {
						it.dohyun.recipe_hub.model.RecipeDto r = recipeDao.getRecipe(rid);
						if (r != null) likedRecipes.add(r);
					} catch (Exception ex) { /* skip missing recipes */ }
				}
			} catch (Exception ex) {
				logger.log(Level.FINE, "liked recipes load failed", ex);
			}
			
			// member에 저장된 dto 값을 통해 접근 가능
			req.setAttribute("member", dto);
			req.setAttribute("likedRecipes", likedRecipes);
			req.getRequestDispatcher("my.jsp").forward(req, resp);
			
		} catch (SQLException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "회원 정보 불러오는 중 에러 발생", e);
			req.setAttribute("error", "회원 정보를 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
			req.getRequestDispatcher("index.jsp").forward(req, resp); // 에러 발생 시 홈으로 이동
		}
	}
}
