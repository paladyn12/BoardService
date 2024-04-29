package BoardService.myproject.dto.board;

import BoardService.myproject.domain.BoardCategory;
import BoardService.myproject.domain.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardCntDto {

    private long totalBoardCnt;
    private long totalNoticeCnt;
    private long totalGreetingCnt;
    private long totalFreeCnt;
    private long totalGoldCnt;

}
