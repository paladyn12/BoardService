package BoardService.myproject.service;

import BoardService.myproject.domain.Board;
import BoardService.myproject.domain.Comment;
import BoardService.myproject.domain.User;
import BoardService.myproject.domain.UserRole;
import BoardService.myproject.dto.comment.CommentCreateRequest;
import BoardService.myproject.repository.BoardRepository;
import BoardService.myproject.repository.CommentRepository;
import BoardService.myproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private UserRepository userRepository;

    public void writeComment(Long boardId, CommentCreateRequest req, String loginId) {
        Board board = boardRepository.findById(boardId).get();
        User user = userRepository.findByLoginId(loginId).get();
        board.commentChange(board.getCommentCnt() + 1);
        commentRepository.save(req.toEntity(board, user));
    }

    public List<Comment> findAll(Long boardId) {
        return commentRepository.findAllByBoardId(boardId);
    }

    @Transactional
    public Long editComment(Long commitId, String newBody, String loginId) {
        Optional<Comment> optComment = commentRepository.findById(commitId);
        Optional<User> optUser = userRepository.findByLoginId(loginId);
        // comment나 user가 없거나 둘의 user 정보가 일치하지 않으면 수정 불가
        if (optComment.isEmpty() || optUser.isEmpty() || !optComment.get().getUser().equals(optUser.get())) {
            return null;
        }

        Comment comment = optComment.get();
        comment.update(newBody);

        return comment.getId();
    }

    public Long deleteComment(Long commentId, String loginId) {
        Optional<Comment> optComment = commentRepository.findById(commentId);
        Optional<User> optUser = userRepository.findByLoginId(loginId);
        // comment나 user가 없거나 ADMIN이 아니며 user정보가 일치하지 않으면 삭제 불가
        // user정보가 일치하거나 ADMIN이면 삭제 가능
        if (optComment.isEmpty() || optUser.isEmpty() ||
                (!optComment.get().getUser().equals(optUser.get()) && !optUser.get().getUserRole().equals(UserRole.ADMIN))) {
            return null;
        }

        Board board = optComment.get().getBoard();
        board.commentChange(board.getCommentCnt() - 1);

        commentRepository.delete(optComment.get());
        return board.getId();
    }
}
