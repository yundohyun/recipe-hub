package it.dohyun.recipe_hub.controller.member;

import it.dohyun.recipe_hub.dao.MemberDao;
import it.dohyun.recipe_hub.model.MemberDto;
import it.dohyun.recipe_hub.util.URLEncodeParser;
import it.dohyun.recipe_hub.util.firebase.FirebaseStorageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.*;
import org.json.JSONObject;

@WebServlet("/member")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024)
public class MemberServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(MemberServlet.class.getName());
  private final MemberDao dao = new MemberDao();

  private void sendResponse(HttpServletResponse res, int status, String message)
      throws IOException {
    res.setContentType("application/json; charset=UTF-8");
    res.setStatus(status);
    JSONObject result = new JSONObject();
    result.put("message", message);
    res.getWriter().write(result.toString());
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("loginId") == null) {
      this.sendResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
      return;
    }

    String id = (String) session.getAttribute("loginId");

    // Map 클래스 params
    Map<String, String> params;
    try {
      params = URLEncodeParser.parseUrlEncodedBody(req);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "요청 Body 파싱 중 오류 발생", e);
      this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "요청 데이터를 해석하는 중 오류가 발생했습니다.");
      return;
    }

    String email = params.get("email");
    String nickname = params.get("nickname");
    String avatar = params.get("avatar");
    String introduce = params.get("introduce");

    try {
      MemberDto dto = dao.getMember(id);
      if (dto == null) {
        // 세션은 있으나, 모종의 이유로 회원 정보를 찾을 수 없는 경우
        // 바로 JSON으로 에러 메시지 반환
        session.invalidate();
        this.sendResponse(
            res, HttpServletResponse.SC_UNAUTHORIZED, "회원 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
        return;
      }

      // 이메일 변경 시 유효성 검사
      if (email == null
          || email.isBlank()
          || !email.matches("^[A-Za-z0-9]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        return;
      }

      // 닉네임 변경 시 유효성 검사
      if (nickname == null || nickname.isBlank()) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "닉네임은 필수 입력칸입니다.");
        return;
      }

      // 사용자가 입력 한 이메일이 본인의 원래 이메일과 다르면서, 고유한 값이 아닐 시 에러
      if (dao.checkEmailExist(email) && !Objects.requireNonNull(email).equals(dto.getEmail())) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이미 사용 중인 이메일입니다.");
        return;
      }

      // 사용자가 입력 한 닉네임이 본인의 원래 닉네임과 다르면서, 고유한 값이 아닐 시 에러
      if (dao.checkNicknameExist(nickname)
          && !Objects.requireNonNull(nickname).equals(dto.getNickname())) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이미 사용 중인 닉네임입니다.");
        return;
      }

      // 값 업데이트
      dto.setEmail(email);
      dto.setNickname(nickname);
      dto.setAvatar(avatar);
      dto.setIntroduce(introduce);

      dao.updateMember(dto);

      this.sendResponse(res, HttpServletResponse.SC_OK, "회원 정보가 성공적으로 수정되었습니다.");

    } catch (Exception e) {
      logger.log(Level.SEVERE, "회원 정보 수정 중 에러 발생", e);
      this.sendResponse(
          res,
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "회원 정보 수정 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    // 처리: multipart/form-data로 전송된 프로필(아바타 포함) 업데이트
    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("loginId") == null) {
      this.sendResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
      return;
    }

    String id = (String) session.getAttribute("loginId");

    try {
      MemberDto dto = dao.getMember(id);
      if (dto == null) {
        session.invalidate();
        this.sendResponse(
            res, HttpServletResponse.SC_UNAUTHORIZED, "회원 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
        return;
      }

      // text fields는 multipart라도 getParameter로 얻을 수 있음
      String email = req.getParameter("email");
      String nickname = req.getParameter("nickname");
      String introduce = req.getParameter("introduce");

      // 이메일 유효성
      if (email == null
          || email.isBlank()
          || !email.matches("^[A-Za-z0-9]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        return;
      }

      if (nickname == null || nickname.isBlank()) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "닉네임은 필수 입력칸입니다.");
        return;
      }

      if (dao.checkEmailExist(email) && !Objects.equals(email, dto.getEmail())) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이미 사용 중인 이메일입니다.");
        return;
      }

      if (dao.checkNicknameExist(nickname) && !Objects.equals(nickname, dto.getNickname())) {
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "이미 사용 중인 닉네임입니다.");
        return;
      }

      // avatar 처리 (Firebase Storage 업로드)
      Part avatarPart = null;
      try {
        avatarPart = req.getPart("avatar");
      } catch (IllegalStateException ise) {
        logger.log(Level.WARNING, "파일 업로드 크기 초과", ise);
        this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "업로드 파일이 너무 큽니다.");
        return;
      }

      if (avatarPart != null && avatarPart.getSize() > 0) {
        String submitted = getSubmittedFileName(avatarPart);
        String ext = getFileExtension(submitted);
        if (!isAllowedImageExt(ext)) {
          this.sendResponse(res, HttpServletResponse.SC_BAD_REQUEST, "허용되지 않는 이미지 형식입니다.");
          return;
        }

        // upload to Firebase Storage using FirebaseStorageUtil
        String filename = id + "_" + System.currentTimeMillis() + ext;
        try (InputStream is = avatarPart.getInputStream()) {
          String publicUrl =
              FirebaseStorageUtil.uploadFile(filename, is, avatarPart.getContentType());
          dto.setAvatar(publicUrl);
        } catch (Exception ex) {
          logger.log(Level.SEVERE, "Firebase Storage 업로드 실패", ex);
          this.sendResponse(
              res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다.");
          return;
        }
      } else {
        // no file uploaded
      }

      // 업데이트 적용
      dto.setEmail(email);
      dto.setNickname(nickname);
      dto.setIntroduce(introduce);

      dao.updateMember(dto);

      this.sendResponse(res, HttpServletResponse.SC_OK, "회원 정보가 성공적으로 수정되었습니다.");

    } catch (SQLException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "회원 정보 수정 중 에러 발생", e);
      this.sendResponse(
          res,
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "회원 정보 수정 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
  }

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

  private static boolean isAllowedImageExt(String ext) {
    if (ext == null) return false;
    String e = ext.toLowerCase();
    return e.equals(".jpg")
        || e.equals(".jpeg")
        || e.equals(".png")
        || e.equals(".webp")
        || e.equals(".gif");
  }
}
