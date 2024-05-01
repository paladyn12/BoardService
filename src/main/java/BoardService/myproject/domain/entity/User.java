package BoardService.myproject.domain.entity;

import BoardService.myproject.domain.enum_class.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId; // 로그인 아이디
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private LocalDateTime createdAt; // 회원 가입 시간
    private Integer receivedLikeCnt; // 유저가 받은 좋아요 수 (본인 제외)

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Board> boards = new ArrayList<>(); // 작성글

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Like> likes = new ArrayList<>(); // 유저가 누른 좋아요

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // 유저가 작성한 댓글

    public void rankUp(UserRole userRole, Authentication auth){
        this.userRole = userRole;

        List<GrantedAuthority> updateAuthorities = new ArrayList<>();
        updateAuthorities.add(new SimpleGrantedAuthority(userRole.name()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updateAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
    public void likeChange(Integer receivedLikeCnt) {
        this.receivedLikeCnt = receivedLikeCnt;
        if (this.receivedLikeCnt >= 10 && this.userRole.equals(UserRole.SILVER)) {
            this.userRole = UserRole.GOLD;
        }
    }

    public void edit(String newPassword, String newNickname) {
        this.password = newPassword;
        this.nickname = newNickname;
    }

    public void changeRole() {
        if (userRole.equals(UserRole.BRONZE)) userRole = UserRole.SILVER;
        else if (userRole.equals(UserRole.SILVER)) userRole = UserRole.GOLD;
        else if (userRole.equals(UserRole.GOLD)) userRole = UserRole.BLACKLIST;
        else if (userRole.equals(UserRole.BLACKLIST)) userRole = UserRole.BRONZE;
    }

}
