package it.dohyun.recipe_hub.controller.auth;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import it.dohyun.recipe_hub.util.URLEncodeParser;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// 비밀번호 관리 서블릿
@WebServlet("/password")
public class ChangePasswordServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(ChangePasswordServlet.class.getName());
	private final MemberDao dao = new MemberDao();

	// 결과를 Json으로 만듦
	private void writeJson(HttpServletResponse resp, int status, Map<String , Object> body)
		throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json; charset=UTF-8");

		StringBuilder json = new StringBuilder();
		json.append("{");

		boolean first = true;
		for (Map.Entry<String, Object> entry : body.entrySet()) {
			if (!first) json.append(",");
			first = false;

			json.append("\"").append(entry.getKey()).append("\":");

			Object value = entry.getValue();

			// 값이 없을 경우
			if (value == null) {
				json.append("null");
			} else if	(value instanceof Number || value instanceof Boolean) {
				json.append(value);
			} else {
				String s = value.toString().replace("\"", "\\\"");
				json.append("\"").append(s).append("\"");
			}
		}

		json.append("}");

		try (PrintWriter out = resp.getWriter()) {
			out.print(json);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		HttpSession session = req.getSession(false);

		// 로그인 여부 확인
		if (session == null || session.getAttribute("loginId") == null) {
			Map<String, Object> body = new HashMap<>();
			body.put("success", false);
			body.put("message", "로그인이 필요합니다.");
			writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED, body);
			return;
		}

		String id = (String) session.getAttribute("loginId");

		Map<String , String> params;

		try {
			params = URLEncodeParser.parseUrlEncodedBody(req);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "요청 본문 파싱 중 오류 발생");
			Map<String, Object> body = new HashMap<>();
			body.put("success", false);
			body.put("message", "요청 데이터를 해석하는 중 오류가 발생했습니다.");
			writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, body);
			return;
		}

		String currentPassword = params.get("currentPassword");
		String newPassword = params.get("newPassword");

		Map<String, Object> errors = new HashMap<>();

		// 새로운 비밀번호 및 비밀번호 확인 유효성 검사
		// 오류 메시지 : newPasswordError
		if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 32) {
			errors.put("newPasswordError", "비밀번호는 8자 이상, 32자 이하여야 합니다.");
		}

		if (!errors.isEmpty()) {
			Map<String, Object> body = new HashMap<>();
			body.put("success", false);
			body.put("errors", errors);
			writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, body);
			return;
		}

		try {
			MemberDto dto = dao.getMember(id);

			// 세션은 있으나, 모종의 이유로 회원 정보를 찾을 수 없는 경우
			if (dto == null) {
				session.invalidate();
				Map<String, Object> body = new HashMap<>();
				body.put("success", false);
				body.put("message", "회원 정보를 찾을 수 없습니다. 다시 로그인 해주세요.");
				writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED, body);
				return;
			}

			String hashedPassword = dto.getPassword();

			// 해시된 현재 비밀번호를 가져와, 일치하는지 검사
			// 오류 메시지 : currentPasswordError
			if (!BCrypt.checkpw(currentPassword, hashedPassword)) {
				Map<String, Object> body = new HashMap<>();
				body.put("success", false);
				body.put("currentPasswordError", "현재 비밀번호가 일치하지 않습니다.");
				writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, body);
				return;
			}

			// 비밀번호 변경
			dao.updateMemberPassword(id, newPassword);

			// 성공 시
			Map<String, Object> body = new HashMap<>();
			body.put("success", true);
			body.put("message", "비밀번호가 성공적으로 변경되었습니다.");
			writeJson(resp, HttpServletResponse.SC_OK, body);

		} catch (SQLException | ClassNotFoundException e) {
			// 비밀번호 변경 중 오류 발생
			logger.log(Level.SEVERE, "비밀번호 변경 중 오류가 발생했습니다.");
			Map<String, Object> body = new HashMap<>();
			body.put("success", false);
			body.put("message", "비밀번호 변경 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
			writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, body);
		}
	}
}
