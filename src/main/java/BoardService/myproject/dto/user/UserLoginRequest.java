package BoardService.myproject.dto.user;

import lombok.Data;

@Data
// 로그인 시 사용되는 DTO
public class UserLoginRequest {
    private String loginId;
    private String password;
}
