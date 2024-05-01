package BoardService.myproject.service;

import BoardService.myproject.domain.entity.Board;
import BoardService.myproject.domain.entity.Like;
import BoardService.myproject.domain.entity.User;
import BoardService.myproject.repository.BoardRepository;
import BoardService.myproject.repository.LikeRepository;
import BoardService.myproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/**
 * addLike(loginId, boardId) : 좋아요를 누르는 로그인 ID와 좋아요가 눌리는 게시글 조회
    게시글 주인과 로그인 ID가 같으면 자기 게시글에 좋아요를 누르는 것이므로 User 계층의 like는 올라가지 않음
 * deleteLike(loginId, boardId) : 좋아요를 삭제하는 로그인 ID와 좋아요가 삭제되는 게시글 조회
    addLike와 마찬가지로 같은 사람이 좋아요를 삭제한 경우 User 계층의 like는 내려가지 않음
 * checkLike(loginId, boardId) : User가 해당 게시글에 좋아요를 눌렀는지 true/false
 */
public class LikeService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public void addLike(String loginId, Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        User loginUser = userRepository.findByLoginId(loginId).get();
        User boardUser = board.getUser();

        // 자신이 누른 좋아요가 아님
        if (!boardUser.equals(loginUser)) {
            boardUser.likeChange(boardUser.getReceivedLikeCnt() + 1);
        }
        board.likeChange(board.getLikeCnt() + 1);
        likeRepository.save(Like.builder()
                        .board(board)
                        .user(loginUser)
                        .build());
    }

    @Transactional
    public void deleteLike(String loginId, Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        User loginUser = userRepository.findByLoginId(loginId).get();
        User boardUser = board.getUser();

        // 자신이 누른 좋아요가 아님
        if (!boardUser.equals(loginUser)) {
            boardUser.likeChange(boardUser.getReceivedLikeCnt() - 1);
        }
        board.likeChange(board.getLikeCnt() - 1);
        likeRepository.deleteByUserLoginIdAndBoardId(loginId, boardId);
    }

    public Boolean checkLike(String loginId, Long boardId) {
        return likeRepository.existsByUserLoginIdAndBoardId(loginId, boardId);
    }
}
