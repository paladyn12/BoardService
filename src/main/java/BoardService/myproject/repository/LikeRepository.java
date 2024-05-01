package BoardService.myproject.repository;

import BoardService.myproject.domain.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByUserLoginId(String loginId);
    Boolean existsByUserLoginIdAndBoardId(String loginId, Long boardId);
    void deleteByUserLoginIdAndBoardId(String loginId, Long boardId);
}
