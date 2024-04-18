package BoardService.myproject.handler;

import BoardService.myproject.repository.UserRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {
    public MyLoginSuccessHandler(UserRepository userRepository) {
    }
}
