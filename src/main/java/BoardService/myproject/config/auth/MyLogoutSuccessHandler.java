package BoardService.myproject.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;

// 로그아웃에 성공한 경우 호출
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 메세지 출력 후 redirect
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        pw.println("<script>alert('로그아웃 되었습니다.'); location.href='/';</script>");
        pw.flush();
    }
}
