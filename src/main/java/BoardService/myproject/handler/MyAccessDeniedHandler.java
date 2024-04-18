package BoardService.myproject.handler;

import BoardService.myproject.domain.User;
import BoardService.myproject.domain.UserRole;
import BoardService.myproject.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

@AllArgsConstructor
// 인가 실패(일반 유저가 관리자 권한이 필요한 URL 접근)시 호출됨
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    private final UserRepository userRepository;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = null;
        if(auth!=null){
            loginUser = userRepository.findByLoginId(auth.getName()).get();
        }
        String requestURI = request.getRequestURI();

        // 로그인 한 유저가 로그인, 회원가입 URI를 요청
        if (requestURI.contains("/users/login")||requestURI.contains("/users/join")){
            // 메세지 출력 후 redirect
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('이미 로그인 되어있습니다!'); location.ref='/';</script>");
            pw.flush();
        }
        
        // 골드게시판은 GOLD, ADMIN만 접근 가능
        else if (requestURI.contains("gold")) {
            // 메세지 출력 후 redirect
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('골드 등급 이상의 유저만 접근 가능합니다!'); location.ref='/';</script>");
            pw.flush();
        } else if (loginUser != null && loginUser.getUserRole().equals(UserRole.BLACKLIST)) {
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('블랙리스트는 글, 댓글 작성이 불가능합니다.'); location.ref='/';</script>");
            pw.flush();
        }
        // BRONZE 등급이 자유게시판에 글을 작성하려는 경우
        else if (requestURI.contains("free/write")) {
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('가인인사 작성 후 작성 가능합니다!'); location.ref='/';</script>");
            pw.flush();
        }
        // SILVER 등급 이상이 가입인사를 작성하려는 경우
        else if (requestURI.contains("greeting")) {
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('가입인사는 한 번만 작성 가능합니다!'); location.ref='/';</script>");
            pw.flush();
        }
        // ADMIN이 아닌데 관리자 페이지에 접근한 경우
        else if (requestURI.contains("admin")) {
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('관리자만 접속 가능합니다!'); location.ref='/';</script>");
            pw.flush();
        }
    }
}
