package it.dohyun.recipe_hub.controller.calories;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/calories")
public class CaloriesServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("loginId") == null) {
      resp.sendRedirect("/login");
      return;
    }

    req.getRequestDispatcher("calories.jsp").forward(req, resp);
  }
}
