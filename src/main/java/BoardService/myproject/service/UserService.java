package BoardService.myproject.service;

import BoardService.myproject.domain.Comment;
import BoardService.myproject.domain.Like;
import BoardService.myproject.domain.User;
import BoardService.myproject.domain.UserRole;
import BoardService.myproject.dto.user.UserCntDto;
import BoardService.myproject.dto.user.UserDto;
import BoardService.myproject.dto.user.UserJoinRequest;
import BoardService.myproject.repository.CommentRepository;
import BoardService.myproject.repository.LikeRepository;
import BoardService.myproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Service
@RequiredArgsConstructor
/**
 * BindingResult joinVaild(UserJoinRequest, BindingResult) : 회원가입 요청 정보의 유효성 검사
 * join(UserJoinRequest) : 회원가입 기능. 유효성 검사 통과 시 호출
    encode된 비밀번호가 들어감
 * myInfo(loginId) : 로그인 ID로 회원 조회
 * BindingResult editValid(UserDto, BindingResult, loginId) : 개인정보 수정 요청 정보의 유효성 검사
 * edit(UserDto, loginId) : 개인정보 수정 기능. 유효성 검사 통과 시 호출
    새 비밀번호를 입력하지 않으면 기존 비밀번호를 사용
 * delete(loginId, nowPassword) : 비밀번호가 일치하면 User 정보 삭제
    User가 누른 좋아요와 작성한 댓글도 함께 삭제, orphanRemoval을 true로 했기 때문에 개수만 조작
 * findAllByNickname(nickname, PageRequest) : nickname을 포함하는 모든 유저 조회
 * changeRole(userId) : User의 changeRole() 호출
 * getUserCnt() : 유저 수 조회를 위한 UserCntDto 반환
 */
public class UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder encoder;

    public BindingResult joinVaild(UserJoinRequest req, BindingResult bindingResult)
    {
        if (req.getLoginId().isEmpty()) {
            bindingResult.addError(new FieldError("req", "loginId", "아이디가 비어있습니다."));
        } else if (req.getLoginId().length()>10){
            bindingResult.addError(new FieldError("req", "loginId", "아이디가 10자를 넘습니다."));
        } else if (userRepository.existsByLoginId(req.getLoginId())) {
            bindingResult.addError(new FieldError("req", "loginId", "아이디가 중복됩니다."));
        }

        if (req.getPassword().isEmpty()) {
            bindingResult.addError(new FieldError("req", "password", "비밀번호가 비어있습니다."));
        }
        
        if (!req.getPassword().equals(req.getPasswordCheck())) {
            bindingResult.addError(new FieldError("req", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if (req.getNickname().isEmpty()) {
            bindingResult.addError(new FieldError("req", "nickname", "닉네임이 비어있습니다."));
        } else if (req.getNickname().length()>10) {
            bindingResult.addError(new FieldError("req", "nickname", "닉네임이 10자를 넘습니다."));
        } else if (userRepository.existsByNickname(req.getNickname())) {
            bindingResult.addError(new FieldError("req", "nickname", "닉네임이 중복됩니다."));
        }

        return bindingResult;
    }

    public void join(UserJoinRequest req) {
        userRepository.save(req.toEntity(encoder.encode(req.getPassword()) ));
    }

    public User myInfo(String loginId) {
        return userRepository.findByLoginId(loginId).get();
    }

    public BindingResult editValid(UserDto dto, BindingResult bindingResult, String loginId)
    {
        User loginUser = userRepository.findByLoginId(loginId).get();

        if (dto.getNowPassword().isEmpty()) {
            bindingResult.addError(new FieldError("dto", "nowPassword", "현재 비밀번호가 비어있습니다."));
        } else if (!encoder.matches(dto.getNowPassword(),loginUser.getPassword())) {
            bindingResult.addError(new FieldError("dto", "nowPassword", "현재 비밀번호가 틀렸습니다."));
        }

        if (!dto.getNewPassword().equals(dto.getNewPasswordCheck())){
            bindingResult.addError(new FieldError("dto", "newPasswordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if (dto.getNickname().isEmpty()) {
            bindingResult.addError(new FieldError("dto", "nickname", "닉네임이 비어있습니다."));
        } else if (dto.getNickname().length()>10) {
            bindingResult.addError(new FieldError("dto", "nickname", "닉네임이 10자를 넘습니다."));
        } else if (!dto.getNickname().equals(loginUser.getNickname()) && userRepository.existsByNickname(dto.getNickname())) {
            bindingResult.addError(new FieldError("dto", "nickname", "닉네임이 중복됩니다."));
        }

        return bindingResult;
    }

    @Transactional
    public void edit(UserDto dto, String loginId) {
        User loginUser = userRepository.findByLoginId(loginId).get();

        if (dto.getNewPassword().equals("")) {
            loginUser.edit(loginUser.getPassword(), dto.getNickname());
        } else {
            loginUser.edit(encoder.encode(dto.getNewPassword()), dto.getNickname());
        }
    }

    @Transactional
    public Boolean delete(String loginId, String nowPassword) {
        User loginUser = userRepository.findByLoginId(loginId).get();

        if(encoder.matches(nowPassword, loginUser.getPassword())) {
            List<Like> likes = likeRepository.findAllByUserLoginId(loginId);
            for (Like like : likes) {
                like.getBoard().likeChange(like.getBoard().getLikeCnt() - 1);
            }

            List<Comment> comments = commentRepository.findAllByUserLoginId(loginId);
            for (Comment comment : comments) {
                comment.getBoard().commentChange(comment.getBoard().getCommentCnt() - 1);
            }

            userRepository.delete(loginUser);

            return true;
        } else {
            return false;
        }
    }

    public Page<User> findAllByNickname(String nickname, PageRequest pageRequest) {
        return userRepository.findAllByNicknameContains(nickname, pageRequest);
    }

    @Transactional
    public void changeRole(Long userId) {
        User user = userRepository.findById(userId).get();
        user.changeRole();
    }

    public UserCntDto getUserCnt() {
        return UserCntDto.builder()
                .totalUserCnt(userRepository.count())
                .totalAdminCnt(userRepository.countAllByUserRole(UserRole.ADMIN))
                .totalBronzeCnt(userRepository.countAllByUserRole(UserRole.BRONZE))
                .totalSilverCnt(userRepository.countAllByUserRole(UserRole.SILVER))
                .totalGoldCnt(userRepository.countAllByUserRole(UserRole.GOLD))
                .totalBlacklistCnt(userRepository.countAllByUserRole(UserRole.BLACKLIST))
                .build();
    }

}
