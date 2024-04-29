package BoardService.myproject.dto.board;

import BoardService.myproject.domain.Board;
import BoardService.myproject.domain.BoardCategory;
import BoardService.myproject.domain.UploadImage;
import BoardService.myproject.domain.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
// Board를 입력받아 DB에 저장할 때 사용하는 DTO
public class BoardCreateRequest {

    private String title;
    private String body;
    private MultipartFile uploadImage;

    public Board toEntity(BoardCategory category, User user) {
        return Board.builder()
                .user(user)
                .category(category)
                .title(title)
                .body(body)
                .likeCnt(0)
                .commentCnt(0)
                .build();
    }
}
