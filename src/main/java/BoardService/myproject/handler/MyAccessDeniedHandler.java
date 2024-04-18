package BoardService.myproject.handler;

import BoardService.myproject.repository.UserRepository;
import org.springframework.security.web.access.AccessDeniedHandler;

public class MyAccessDeniedHandler implements AccessDeniedHandler {
    public MyAccessDeniedHandler(UserRepository userRepository) {
    }
}
