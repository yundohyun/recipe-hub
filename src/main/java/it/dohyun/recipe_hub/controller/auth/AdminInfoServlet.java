package it.dohyun.recipe_hub.controller.auth;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// 관리자 페이지 : adminMy.jsp
@WebServlet("/admin")
public class AdminInfoServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(AdminInfoServlet.class.getName());
	private final MemberDao dao = new MemberDao();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);

		if (session == null || session.getAttribute("loginId") == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		String loginId = (String) session.getAttribute("loginId");

		try {
			// 관리자 여부 확인
			MemberDto me = dao.getMember(loginId);
			if (me == null || !me.isAdmin()) {
				resp.sendRedirect(req.getContextPath() + "/my");
				return;
			}

			// 회원 목록 조회
			ArrayList<MemberDto> members = dao.getMembers();
			req.setAttribute("members", members);
			// 조회된 내용은 members 변수에 담은 후, 포워드
			req.getRequestDispatcher("adminMy.jsp").forward(req, resp);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "관리자 페이지 조회 중 오류가 발생했습니다.", e);
			req.setAttribute("error", "관리자 페이지 조회 중 오류가 발생했습니다.");
			req.getRequestDispatcher("adminMy.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	}