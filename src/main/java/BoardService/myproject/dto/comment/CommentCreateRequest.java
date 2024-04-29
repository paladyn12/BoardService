package BoardService.myproject.dto.comment;

import BoardService.myproject.domain.Board;
import BoardService.myproject.domain.Comment;
import BoardService.myproject.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
public class CommentCreateRequest {

    private String body;

    public Comment toEntity(Board board, User user) {
        return Comment.builder()
                .board(board)
                .user(user)
                .body(body)
                .build();
    }

}
