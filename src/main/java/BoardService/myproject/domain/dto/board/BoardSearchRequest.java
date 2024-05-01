package BoardService.myproject.domain.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
// 게시글 리스트에서 검색할 때 사용하는 DTO
public class BoardSearchRequest {

    private String sortType;
    private String searchType;
    private String keyword;
}
