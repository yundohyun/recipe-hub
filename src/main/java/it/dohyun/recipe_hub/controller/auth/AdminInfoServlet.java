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
@WebServlet("/adminMy")
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
		req.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession(false);

		if (session == null || session.getAttribute("loginId") == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		String loginId = (String) session.getAttribute("loginId");

		//삭제와 수정에 따른 분기
		String action = req.getParameter("action");
		String memberId = req.getParameter("memberId");

		if (memberId == null || memberId.isBlank()) {
			// 타겟 회원 ID 없으면 그냥 관리자 페이지로 다시
			resp.sendRedirect(req.getContextPath() + "/adminMy");
			return;
		}

		try {
			// 관리자가 맞는지 확인
			MemberDto me = dao.getMember(loginId);
			if (me == null || !me.isAdmin()) {
				resp.sendRedirect(req.getContextPath() + "/my");
				return;
			}

			// action의 값이 update일 경우, 값을 수정
			if ("update".equals(action)) {
				MemberDto target = dao.getMember(memberId);
				if (target == null) {
					resp.sendRedirect(req.getContextPath() + "/adminMy");
					return;
				}

				// 부적절한 닉네임, 아바타, 자기소개는 관리자 권한으로 수정 가능
				String nickname = req.getParameter("nickname");
				String avatar = req.getParameter("avatar");
				String introduction = req.getParameter("introduction");

				if (nickname != null && !nickname.isBlank()) {
					target.setNickname(nickname);
				}
				if (avatar != null) {
					target.setAvatar(avatar);
				}
				if (introduction != null) {
					target.setIntroduce(introduction);
				}

				dao.updateMember(target);

				// action의 값이 delete일 경우, 값을 제거
			} else if ("delete".equals(action)) {
				dao.deleteMember(memberId);
			}
			resp.sendRedirect(req.getContextPath() + "/adminMy");

		} catch (SQLException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "관리자 권한으로 수정/삭제 중 오류가 발생했습니다. ", e);
			req.setAttribute("error", "관리자 권한으로 수정/삭제 중 오류가 발생했습니다.");
			req.getRequestDispatcher("adminMy.jsp").forward(req, resp);
		}
	}

}
