package BoardService.myproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCntDto {

    private long totalUserCnt;
    private long totalAdminCnt;
    private long totalBronzeCnt;
    private long totalSilverCnt;
    private long totalGoldCnt;
    private long totalBlacklistCnt;

}
