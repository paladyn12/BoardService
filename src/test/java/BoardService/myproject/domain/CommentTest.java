package BoardService.myproject.domain;

import BoardService.myproject.domain.entity.Comment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    public void BaseTimeEntity_등록() throws Exception{
        //given
        LocalDateTime now = LocalDateTime.of(2024,4,14,10,44);


        //when
        Comment comment = new Comment();
        System.out.println(comment.getCreatedAt());

        //then
        assertThat(comment.getCreatedAt()).isAfter(now);

    }

}