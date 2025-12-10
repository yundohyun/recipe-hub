package it.dohyun.recipe_hub.controller.recipe;

import it.dohyun.recipe_hub.dao.*;
import it.dohyun.recipe_hub.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

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
    ImageDao imageDao = new ImageDao();
    MemberDao memberDao = new MemberDao();

    try {
      RecipeDto recipe = recipeDao.getRecipe(id);
      if (recipe == null) {
        resp.sendRedirect(req.getContextPath() + "/");
        return;
      }

      List<RecipeIngredientDto> ingredients = ingredientDao.getRecipeIngredientsByRecipeId(id);
      List<RecipeContentDto> contents = contentDao.getRecipeContents(id);

      // map content id -> list of image urls
      Map<String, List<ImageDto>> contentImages = new HashMap<>();
      for (RecipeContentDto cd : contents) {
        List<RecipeContentImageDto> imgs = contentImageDao.getRecipeContentImages(cd.getId());
        List<ImageDto> imageDtos = new ArrayList<>();
        for (RecipeContentImageDto rci : imgs) {
          ImageDto im = imageDao.getImage(rci.getImageId());
          if (im != null) imageDtos.add(im);
        }
        contentImages.put(cd.getId(), imageDtos);
      }

      RecipeCaloriesDto rc = recipeCaloriesDao.getRecipeCalories(id);
      MemberDto author = memberDao.getMember(recipe.getMemberId());

      req.setAttribute("recipe", recipe);
      req.setAttribute("ingredients", ingredients);
      req.setAttribute("contents", contents);
      req.setAttribute("contentImages", contentImages);
      req.setAttribute("recipeCalories", rc);
      req.setAttribute("author", author);

      req.getRequestDispatcher("/recipe/view.jsp").forward(req, resp);

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "레시피 조회 중 오류", e);
      resp.sendRedirect(req.getContextPath() + "/");
    }
  }
}

