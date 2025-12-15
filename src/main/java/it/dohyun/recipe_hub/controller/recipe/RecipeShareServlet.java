package it.dohyun.recipe_hub.controller.recipe;

import it.dohyun.recipe_hub.dao.*;
import it.dohyun.recipe_hub.model.*;
import it.dohyun.recipe_hub.util.firebase.FirebaseStorageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/recipe/share")
@MultipartConfig(
	fileSizeThreshold = 1024 * 1024,
	maxFileSize = 10 * 1024 * 1024,
	maxRequestSize = 50 * 1024 * 1024)
public class RecipeShareServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(RecipeShareServlet.class.getName());
	
	private static String getSubmittedFileName(Part part) {
		String header = part.getHeader("content-disposition");
		if (header == null) return null;
		for (String cd : header.split(";")) {
			if (cd.trim().startsWith("filename")) {
				String fn = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return fn.substring(fn.lastIndexOf(File.separator) + 1);
			}
		}
		return null;
	}
	
	private static String getFileExtension(String name) {
		if (name == null) return "";
		int idx = name.lastIndexOf('.');
		return idx >= 0 ? name.substring(idx) : "";
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		// supply category options to the share form as well
		java.util.Map<String, String> categories = new java.util.LinkedHashMap<>();
		categories.put("etc", "기타");
		categories.put("egg", "계란요리");
		categories.put("street", "분식");
		categories.put("soup", "국&탕");
		categories.put("rice", "밥요리");
		categories.put("pasta", "파스타");
		categories.put("grill", "구이");
		req.setAttribute("categories", categories);
		
		req.getRequestDispatcher("/recipe/share.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("loginId") == null) {
			req.setAttribute("error", "로그인이 필요합니다.");
			req.getRequestDispatcher("/login.jsp").forward(req, resp);
			return;
		}
		
		String memberId = (String) session.getAttribute("loginId");
		
		// Log all multipart parts for debugging (names, sizes, content types)
		try {
			Collection<Part> debugParts = req.getParts();
			StringBuilder sb = new StringBuilder();
			sb.append("Incoming multipart parts:\n");
			for (Part p : debugParts) {
				String pname = p.getName();
				long psize = p.getSize();
				String ptype = p.getContentType();
				String cd = p.getHeader("content-disposition");
				sb.append(
					String.format("part name=%s, size=%d, type=%s, cd=%s\n", pname, psize, ptype, cd));
			}
			logger.log(Level.INFO, sb.toString());
		} catch (Exception ex) {
			logger.log(Level.FINE, "Unable to list multipart parts for debug", ex);
		}
		
		String title = req.getParameter("title");
		String description = req.getParameter("description");
		String difficulty = req.getParameter("difficulty");
		String category = req.getParameter("category");
		String serveStr = req.getParameter("serve");
		String durationStr = req.getParameter("duration");
		String caloriesId = req.getParameter("caloriesId");
		
		// accept both common naming variants: 'ingredient[]' or 'ingredient'
		String[] ingredients = req.getParameterValues("ingredient[]");
		if (ingredients == null) ingredients = req.getParameterValues("ingredient");
		String[] amounts = req.getParameterValues("amount[]");
		if (amounts == null) amounts = req.getParameterValues("amount");
		String[] contents = req.getParameterValues("content[]");
		if (contents == null) contents = req.getParameterValues("content");
		
		// basic validation
		if (title == null || title.isBlank()) {
			req.setAttribute("error", "제목은 필수입니다.");
			req.getRequestDispatcher("/recipe/share.jsp").forward(req, resp);
			return;
		}
		
		int serve = 1;
		int duration = 0;
		try {
			if (serveStr != null && !serveStr.isBlank()) {
				// allow inputs like "2" or "2인분"
				String digits = serveStr.replaceAll("[^0-9]", "");
				if (!digits.isBlank()) serve = Integer.parseInt(digits);
			}
			if (durationStr != null && !durationStr.isBlank()) {
				// allow inputs like "30" or "30분"
				String digits = durationStr.replaceAll("[^0-9]", "");
				if (!digits.isBlank()) duration = Integer.parseInt(digits);
			}
		} catch (NumberFormatException nfe) {
			req.setAttribute("error", "인원수 또는 소요시간 형식이 잘못되었습니다.");
			req.getRequestDispatcher("/recipe/share.jsp").forward(req, resp);
			return;
		}
		
		// create recipe
		RecipeDao recipeDao = new RecipeDao();
		RecipeIngredientDao ingredientDao = new RecipeIngredientDao();
		RecipeContentDao contentDao = new RecipeContentDao();
		RecipeContentImageDao contentImageDao = new RecipeContentImageDao();
		RecipeCaloriesDao recipeCaloriesDao = new RecipeCaloriesDao();
		
		String recipeId = UUID.randomUUID().toString();
		RecipeDto recipe = new RecipeDto();
		recipe.setId(recipeId);
		recipe.setMemberId(memberId);
		recipe.setTitle(title);
		recipe.setDescription(description);
		recipe.setDifficulty(difficulty);
		try {
			java.util.Set<String> allowed = new java.util.HashSet<>(java.util.Arrays.asList("etc", "egg", "street", "soup", "rice", "pasta", "grill"));
			if (category == null || category.isBlank() || !allowed.contains(category)) {
				logger.log(java.util.logging.Level.INFO, "Category missing or invalid (received: " + category + "). Defaulting to 'etc'.");
				recipe.setCategory("etc");
			} else {
				recipe.setCategory(category);
			}
		} catch (Exception ex) {
			logger.log(java.util.logging.Level.WARNING, "Error validating category, defaulting to etc", ex);
			recipe.setCategory("etc");
		}
		recipe.setServe(serve);
		recipe.setDuration(duration);
		recipe.setViewCount(0);
		
		// handle thumbnail upload (single part named 'thumbnail')
		try {
			Part thumbPart = req.getPart("thumbnail");
			if (thumbPart != null && thumbPart.getSize() > 0) {
				String submitted = getSubmittedFileName(thumbPart);
				String ext = getFileExtension(submitted);
				String filename = recipeId + "/thumbnail_" + System.currentTimeMillis() + ext;
				try (InputStream is = thumbPart.getInputStream()) {
					String publicUrl =
						FirebaseStorageUtil.uploadFile(filename, is, thumbPart.getContentType());
					if (publicUrl != null && !publicUrl.isBlank()) {
						recipe.setThumbnail(publicUrl);
					} else {
						logger.log(Level.WARNING, "썸네일 업로드 후 publicUrl이 비어있습니다.");
					}
				} catch (Exception e) {
					logger.log(Level.WARNING, "썸네일 업로드 중 오류", e);
				}
			}
		} catch (IllegalStateException | IOException | ServletException ex) {
			logger.log(Level.WARNING, "썸네일 처리 중 오류", ex);
		}
		
		try {
			recipeDao.createRecipe(recipe);
			
			// ingredients
			if (ingredients != null) {
				for (int i = 0; i < ingredients.length; i++) {
					String ing = ingredients[i];
					String amt = (amounts != null && amounts.length > i) ? amounts[i] : null;
					if (ing == null || ing.isBlank()) continue;
					RecipeIngredientDto ingDto = new RecipeIngredientDto();
					ingDto.setRecipeId(recipeId);
					ingDto.setIngredient(ing);
					ingDto.setAmount(amt);
					ingredientDao.createRecipeIngredient(ingDto);
				}
			}
			
			// contents
			if (contents != null) {
				for (int i = 0; i < contents.length; i++) {
					String c = contents[i];
					if (c == null || c.isBlank()) continue;
					RecipeContentDto contentDto = new RecipeContentDto();
					contentDto.setStep(i + 1);
					contentDto.setRecipeId(recipeId);
					contentDto.setContent(c);
					contentDao.createRecipeContent(contentDto);
				}
			}
			
			// fetch created contents to map step -> id
			List<RecipeContentDto> createdContents = contentDao.getRecipeContents(recipeId);
			Map<Integer, String> stepToContentId = new HashMap<>();
			for (RecipeContentDto cd : createdContents) {
				if (cd.getStep() != null) stepToContentId.put(cd.getStep(), cd.getId());
			}
			
			// For each created step (in ascending order), look for a multipart part named
			// contentImage_<step>
			int handledImages = 0;
			List<Integer> steps = new ArrayList<>(stepToContentId.keySet());
			Collections.sort(steps);
			logger.log(Level.INFO, "Processing content images for steps=" + steps);
			for (Integer step : steps) {
				String contentId = stepToContentId.get(step);
				try {
					Part imgPart = null;
					try {
						imgPart = req.getPart("contentImage_" + step);
					} catch (IllegalStateException | IOException | ServletException ignore) {
						// will be handled below
					}
					if (imgPart == null || imgPart.getSize() == 0) {
						logger.log(Level.FINE, "No content image part for step=" + step);
						continue;
					}
					
					String submitted = getSubmittedFileName(imgPart);
					String ext = getFileExtension(submitted);
					String filename = recipeId + "/content_" + step + "_" + System.currentTimeMillis() + ext;
					try (InputStream is = imgPart.getInputStream()) {
						String publicUrl =
							FirebaseStorageUtil.uploadFile(filename, is, imgPart.getContentType());
						if (publicUrl == null || publicUrl.isBlank()) {
							logger.log(Level.WARNING, "이미지 업로드 후 publicUrl이 비어있습니다. content step=" + step);
						} else {
							RecipeContentImageDto mapping = new RecipeContentImageDto();
							mapping.setRecipeContentId(contentId);
							mapping.setImageUrl(publicUrl);
							contentImageDao.createRecipeContentImage(mapping);
							handledImages++;
							logger.log(
								Level.INFO, "Uploaded content image for step=" + step + " -> " + publicUrl);
						}
					}
				} catch (IllegalStateException | IOException | ServletException ex) {
					logger.log(Level.WARNING, "content image 처리 중 오류 step=" + step, ex);
				} catch (Exception ex) {
					logger.log(Level.WARNING, "이미지 업로드 중 오류 발생 for step=" + step, ex);
				}
			}
			logger.log(
				Level.INFO,
				"Handled content images=" + handledImages + ", createdSteps=" + stepToContentId.size());
			
			// calories mapping
			if (caloriesId != null && !caloriesId.isBlank()) {
				// If caloriesId is provided, simply link it to the recipe
				try {
					RecipeCaloriesDto mapping = new RecipeCaloriesDto();
					mapping.setRecipeId(recipeId);
					mapping.setCaloriesId(caloriesId);
					recipeCaloriesDao.createRecipeCalories(mapping);
				} catch (Exception e) {
					logger.log(Level.WARNING, "레시피-칼로리 매핑 생성 중 오류: " + caloriesId, e);
				}
			} else {
				// If no caloriesId provided, try to fetch 5 related calories via PerplexityUtil using recipe title
				try {
					org.json.JSONArray arr = it.dohyun.recipe_hub.util.PerplexityUtil.fetchNutritionRaw(title);
					logger.log(Level.INFO, "Perplexity response for title='" + title + "': " + (arr == null ? "null" : arr.toString()));
					if (arr != null) {
						// remove any pre-existing mappings (defensive: avoid duplicates)
						try {
							recipeCaloriesDao.deleteRecipeCalories(recipeId);
						} catch (Exception e) {
							logger.log(Level.WARNING, "기존 레시피-칼로리 매핑 삭제 실패 (무시): " + recipeId, e);
						}
						
						int added = 0;
						CaloriesDao caloriesDao = new CaloriesDao();
						for (int i = 0; i < arr.length(); i++) {
							if (added >= 5) break;
							org.json.JSONObject itObj = arr.optJSONObject(i);
							if (itObj == null) continue;
							String name = itObj.optString("name", null);
							if (name == null) continue;
							int serveVal = itObj.optInt("serve", 0);
							int cal = (int) Math.round(itObj.optDouble("calories", 0.0));
							double protein = itObj.optDouble("protein", 0.0);
							double fat = itObj.optDouble("fat", 0.0);
							double carbs = itObj.optDouble("carbohydrates", 0.0);
							try {
								// check existence
								boolean exists = false;
								try {
									exists = caloriesDao.existsByNameAndCalories(name, cal);
								} catch (Exception ex) {
									logger.log(Level.WARNING, "existsByNameAndCalories 실패", ex);
								}
								if (!exists) {
									it.dohyun.recipe_hub.model.CaloriesDto cdto = new it.dohyun.recipe_hub.model.CaloriesDto();
									cdto.setName(name);
									cdto.setServe(serveVal);
									cdto.setCalories(cal);
									cdto.setProtein(protein);
									cdto.setFat(fat);
									cdto.setCarbohydrates(carbs);
									try {
										caloriesDao.createCalories(cdto);
										logger.log(Level.INFO, "Saved new calories: " + name + " = " + cal);
									} catch (Exception ex) {
										logger.log(Level.WARNING, "칼로리 저장 실패: " + name + " / " + cal, ex);
									}
								} else {
									logger.log(Level.INFO, "칼로리 이미 존재: " + name + " = " + cal);
								}
								
								// fetch id (after possible insert)
								String newId = null;
								try {
									newId = caloriesDao.getCaloriesIdByNameAndCalories(name, cal);
								} catch (Exception ex) {
									logger.log(Level.WARNING, "getCaloriesIdByNameAndCalories 실패", ex);
								}
								if (newId != null) {
									try {
										// create mapping
										it.dohyun.recipe_hub.model.RecipeCaloriesDto rc = new it.dohyun.recipe_hub.model.RecipeCaloriesDto();
										rc.setRecipeId(recipeId);
										rc.setCaloriesId(newId);
										recipeCaloriesDao.createRecipeCalories(rc);
										logger.log(Level.INFO, "Linked recipe " + recipeId + " -> calories " + newId + " (" + name + ")");
									} catch (Exception ex) {
										logger.log(Level.WARNING, "레시피-칼로리 매핑 생성 실패 for caloriesId=" + newId, ex);
									}
								} else {
									logger.log(Level.WARNING, "칼로리 id 조회 실패, 매핑 스킵: " + name + " = " + cal);
								}
								
								added++;
							} catch (Exception ex) {
								logger.log(Level.WARNING, "칼로리 저장/연결 중 오류: " + name, ex);
							}
						}
						logger.log(Level.INFO, "Perplexity-derived calories processed, added links=" + added + " for recipe=" + recipeId);
					}
				} catch (Exception e) {
					logger.log(Level.WARNING, "Perplexity 연동 중 오류", e);
				}
			}
			
			// success -> redirect to home or recipe list
			resp.sendRedirect(req.getContextPath() + "/");
			
		} catch (SQLException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "레시피 생성 중 오류", e);
			req.setAttribute("error", "레시피 생성 중 오류가 발생했습니다.");
			req.getRequestDispatcher("/recipe/share.jsp").forward(req, resp);
		}
	}
}
