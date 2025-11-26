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
				// 세션 만료 또는 서버 재시작으로 인한 dto 정보 말소 시 재로그인
				session.invalidate();
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}

			// member에 저장된 dto 값을 통해 접근 가능
			req.setAttribute("member", dto);
			req.getRequestDispatcher("my.jsp").forward(req, resp);

		} catch (SQLException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "회원 정보 불러오는 중 에러 발생", e);
			req.setAttribute("error", "회원 정보를 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
			req.getRequestDispatcher("index.jsp").forward(req, resp); // 에러 발생 시 홈으로 이동
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		HttpSession session = req.getSession(false);

		// 혹시나 로그인이 되지 않았다면, 로그인 페이지로 이동
		if (session == null || session.getAttribute("loginId") == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		String id = (String) session.getAttribute("loginId");
		String action = req.getParameter("action");

		// 회원 탈퇴 기능 (action 값이 delete라면 삭제 진행)
		if("delete".equals(action)) {
			try {
				dao.deleteMember(id);
				session.invalidate();
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			} catch (SQLException | ClassNotFoundException e) {
				logger.log(Level.SEVERE, "회원 탈퇴 중 에러 발생", e);
				req.setAttribute("error", "회원 탈퇴 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
				req.getRequestDispatcher("my.jsp").forward(req, resp);
				return;
			}
		}

		String email = req.getParameter("email");
		String nickname = req.getParameter("nickname");
		String avatar = req.getParameter("avatar");
		String introduce = req.getParameter("introduce");

		boolean hasError = false;

		// 이메일 변경 시 유효성 검사
		if (email == null || email.isBlank()) {
			// 이메일만 해당되는 에러메시지
			req.setAttribute("emailError", "이메일은 필수 입력칸입니다.");
			hasError = true;
		} else if (!email.matches("^[A-Za-z0-9]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			req.setAttribute("emailError", "이메일 형식이 올바르지 않습니다.");
			hasError = true;
		}

		// 닉네임 변경 시 유효성 검사
		if (nickname == null || nickname.isBlank()) {
			// 닉네임만 해당되는 에러메시지
			req.setAttribute("nicknameError", "닉네임은 필수 입력칸입니다.");
			hasError = true;
		}

		// 에러 있을 시, 기존에 입력했던 값은 다시 표시할 수 있게 속성에 저장
		if (hasError) {
			try {
				MemberDto dto = dao.getMember(id);
				if (dto != null) {
					// 사용자가 입력한 값으로 덮어쓰기
					dto.setEmail(email);
					dto.setNickname(nickname);
					dto.setAvatar(avatar);
					dto.setIntroduce(introduce);
					req.setAttribute("member", dto);
				}
			} catch (SQLException | ClassNotFoundException e) {
				logger.log(Level.SEVERE, "회원 정보를 불러오는 도중 에러 발생", e);
			}
			req.getRequestDispatcher("my.jsp").forward(req, resp);
			return;
		}

		// DB를 통해 변경 값이 중복인지 판단 여부와, 문제 없으면 업데이트 하는 코드
		try {
			MemberDto dto = dao.getMember(id);
			if (dto == null) {
				// 세션은 있으나, 모종의 이유로 회원 정보를 찾을 수 없는 경우
				session.invalidate();
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}

			// 사용자가 입력 한 이메일이 본인의 원래 이메일과 다르면서, 고유한 값이 아닐 시 에러
			if (dao.checkEmailExist(email) && !email.equals(dto.getEmail())) {
				req.setAttribute("emailError", "이미 사용 중인 이메일입니다.");
				hasError = true;
			}

			// 사용자가 입력 한 닉네임이 본인의 원래 닉네임과 다르면서, 고유한 값이 아닐 시 에러
			if (dao.checkNicknameExist(nickname) && !nickname.equals(dto.getNickname())) {
				req.setAttribute("nicknameError", "이미 사용 중인 닉네임입니다.");
				hasError = true;
			}

			if (hasError) {
				dto.setEmail(email);
				dto.setNickname(nickname);
				dto.setAvatar(avatar);
				dto.setIntroduce(introduce);

				// hasError 존재 시, 현재 폼의 값을 req 영역에 저장 후, 마이페이지 다시 불러옴
				req.setAttribute("member", dto);
				req.getRequestDispatcher("my.jsp").forward(req, resp);
				return;
			}

			dto.setEmail(email);
			dto.setNickname(nickname);
			dto.setAvatar(avatar);
			dto.setIntroduce(introduce);

			dao.updateMember(dto);

			resp.sendRedirect(req.getContextPath() + "/my");

		} catch (Exception e) {
			logger.log(Level.SEVERE, "회원 정보 수정 중 에러 발생", e);
			req.setAttribute("error", "회원 정보 수정 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
			req.getRequestDispatcher("my.jsp").forward(req, resp);
		}
	}
}
