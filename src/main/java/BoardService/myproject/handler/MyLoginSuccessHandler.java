package BoardService.myproject.handler;

import BoardService.myproject.domain.User;
import BoardService.myproject.domain.UserRole;
import BoardService.myproject.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;

@AllArgsConstructor
// 로그인에 성공한 경우 호출
// 유저에 맞는 메세지 출력 후 세션의 prevPage에 담긴 URL로 이동 (로그인 페이지 요청시 저장됨)
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {
        // 세션 유지 시간 = 3600초
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(3600);

        User loginUser = userRepository.findByLoginId(auth.getName()).get();

        // 성공 시 메세지 출력 후 홈 화면으로 redirect
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        if(loginUser.getUserRole().equals(UserRole.BLACKLIST)){
            pw.println("<script>alert('" + loginUser.getNickname() + "님은 블랙리스트입니다. " +
                    "글, 댓글 작성이 불가합니다.'); location.href='/';</script>");
        } else {
            String prevPage = (String) request.getSession().getAttribute("prevPage");
            if (prevPage != null) {
                pw.println("<script>alert('" + loginUser.getNickname() + "님 반갑습니다!'); " +
                        "location.href='" + prevPage + "';</script>");
            } else {
                pw.println("<script>alert('" + loginUser.getNickname() + "님 반갑습니다!'); " +
                        "location.href='/';</script>");
            }
        }
        pw.flush();
    }
}
