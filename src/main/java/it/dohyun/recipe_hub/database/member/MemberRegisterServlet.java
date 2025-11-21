package it.dohyun.recipe_hub.database.member;

import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/register")
public class MemberRegisterServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(MemberRegisterServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//회원가입 페이지로 포워드 (추후 경로 설정)
		req.getRequestDispatcher("register.jsp").forward(req, resp);
		}

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String nickname = req.getParameter("nickname");


		MemberDto dto = new MemberDto();
		dto.setEmail(email);
		dto.setPassword(password);
		dto.setNickname(nickname);

		MemberDao dao = new MemberDao();

		try {
			dao.setMember(dto);
			//회원가입 성공 시 /login Servlet으로 리다이렉트
			resp.sendRedirect(req.getContextPath() + "/login");

		} catch (SQLException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "회원가입 처리 중 예외 발생", e);
			req.setAttribute("error", "회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.");
			// 실패 시 에러메시지 출력 후, 회원가입 페이지로 다시 포워드
			req.getRequestDispatcher("register.jsp").forward(req, resp);

		}

	}

}
