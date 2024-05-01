package BoardService.myproject.domain.dto.user;

import BoardService.myproject.domain.entity.User;
import BoardService.myproject.domain.enum_class.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
// 회원가입시 사용되는 DTO
public class UserJoinRequest {
    private String loginId;
    private String password;
    private String passwordCheck;
    private String nickname;

    public User toEntity(String encodedPassword){
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .nickname(nickname)
                .userRole(UserRole.BRONZE)
                .createdAt(LocalDateTime.now())
                .receivedLikeCnt(0)
                .build();
    }
}
