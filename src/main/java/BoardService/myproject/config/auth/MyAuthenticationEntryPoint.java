package BoardService.myproject.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

// 인증 실패(로그인하지 않은 유저가 로그인 필요한 URL 접근)시 호출됨
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // 메세지 출력 후 홈으로 redirect
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        pw.println("<script>alert('로그인한 유저만 가능합니다!'); location.ref='/users/login';</script>");
        pw.flush();
    }
}
