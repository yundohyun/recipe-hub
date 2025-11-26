package it.dohyun.recipe_hub.controller.auth;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/member")
public class AdminMemberServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(AdminMemberServlet.class.getName());
	private final MemberDao dao = new MemberDao();

	// 관리자 여부 확인하는 메서드
	private MemberDto checkAdmin(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);

		if (session == null || session.getAttribute("loginId") == null) {
			// 로그인한 아이디가 없는 경우 로그인 페이지로
			resp.sendRedirect(req.getContextPath() + "/login");
			return null;
		}

		String loginId = (String) session.getAttribute("loginId");

		try {
			MemberDto me = dao.getMember(loginId);

			if (me == null || !me.isAdmin()) {
				logger.log(Level.WARNING, "관리자 권한이 없는 사용자의 접근입니다. id=" + loginId);
				resp.sendRedirect(req.getContextPath() + "/my");
				return null;
			}
			return me;
		} catch (SQLException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "관리자 여부 확인 중 오류 발생", e);
			req.setAttribute("error", "관리자 여부 확인 중 오류가 발생했습니다.");
			req.getRequestDispatcher("/admin").forward(req, resp);
			return null;
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		try {
			if(checkAdmin(req, resp) == null) return;

			String memberId = req.getParameter("memberId");
			if (memberId == null || memberId.isBlank()) {
				resp.sendRedirect(req.getContextPath() + "/admin");
				return;
			}

			MemberDto target = dao.getMember(memberId);

			if (target == null) {
				resp.sendRedirect(req.getContextPath() + "/admin");
				return;
			}

			String nickname = req.getParameter("nickname");
			String avatar = req.getParameter("avatar");
			String introduction = req.getParameter("introduction");

			if (nickname != null && !nickname.isBlank()) {
				target.setNickname(nickname);
			}
			if  (avatar != null) {
				target.setAvatar(avatar);
			}
			if (introduction != null) {
				target.setIntroduce(introduction);
			}

			// 회원 정보 업데이트
			dao.updateMember(target);

		} catch (SQLException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "관리자 권한으로 수정 중 오류 발생", e);
			req.setAttribute("error", "관리자 권한으로 수정 중 오류가 발생했습니다.");
			req.getRequestDispatcher("admin.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			if (checkAdmin(req, resp) == null) return;

			String memberId = req.getParameter("memberId");
			if (memberId == null || memberId.isBlank()) {
				resp.sendRedirect(req.getContextPath() + "/admin");
				return;
			}

			dao.deleteMember(memberId);

		} catch (SQLException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "관리자 권한으로 삭제 중 오류 발생", e);
			req.setAttribute("error", "관리자 권한으로 삭제 중 오류가 발생했습니다.");
			req.getRequestDispatcher("admin.jsp").forward(req, resp);
		}
	}
}
