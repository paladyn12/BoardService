package BoardService.myproject.domain.dto.comment;

import BoardService.myproject.domain.entity.Board;
import BoardService.myproject.domain.entity.Comment;
import BoardService.myproject.domain.entity.User;
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
