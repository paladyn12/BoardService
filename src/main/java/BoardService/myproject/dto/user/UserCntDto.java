package BoardService.myproject.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
// 홈 화면에서 각각의 등급에 해당하는 User 수를 출력하기 위해 사용되는 DTO
public class UserCntDto {

    private long totalUserCnt;
    private long totalAdminCnt;
    private long totalBronzeCnt;
    private long totalSilverCnt;
    private long totalGoldCnt;
    private long totalBlacklistCnt;

}
