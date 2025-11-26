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

// 마이페이지 버튼 클릭 시 /adminCheck 서블릿을 통해 관리자인지, 일반 유저인지 검사
// 관리자라면 /adminMy 서블릿으로 이동, 관리자가 아니라면 /my 서블릿으로 이동
@WebServlet("/adminCheck")
public class CheckAdminServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(CheckAdminServlet.class.getName());
	private final MemberDao dao = new MemberDao();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession(false);

		if (session == null || session.getAttribute("loginId") == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		String id = (String) session.getAttribute("loginId");

		try {
			MemberDto dto = dao.getMember(id);

			if (dto == null) {
				// 세션 만료 또는 서버 재시작으로 인한 dto 정보 말소 시 재로그인
				session.invalidate();
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}

			// 어드민 여부 가져옴
			boolean admin = dto.isAdmin();

			if (admin) {
				// 어드민일 시 /adminMy 서블릿으로 이동
				resp.sendRedirect(req.getContextPath() + "/adminMy");
			} else  {
				// 어드민 아닐 시 /my 서블릿으로 (마이페이지)
				resp.sendRedirect(req.getContextPath() + "/my");
			}

		} catch (ClassNotFoundException | SQLException e) {
			logger.log(Level.SEVERE, "관리자 여부 조회 중 에러 발생", e);
			req.setAttribute("error", "관리자 정보를 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
			req.getRequestDispatcher("index.jsp").forward(req, resp);
		}
	}

	//post 방식도 get방식과 동일하게 처리
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
