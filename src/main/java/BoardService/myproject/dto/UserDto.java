package BoardService.myproject.dto;

import BoardService.myproject.domain.User;
import BoardService.myproject.domain.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
// 정보 수정, 탈퇴 등에 사용되는 DTO
public class UserDto {

    private String loginId;
    private String nickname;
    private String nowPassword;
    private String newPassword;
    private String newPasswordCheck;


    public static UserDto of(User user){
        return UserDto.builder()
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .build();
    }
}
