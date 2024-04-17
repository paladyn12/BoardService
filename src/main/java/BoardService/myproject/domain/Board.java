package BoardService.myproject.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Board extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime last_modified_at;

    @Enumerated(EnumType.STRING)
    private BoardCategory category;

    @OneToOne(fetch = FetchType.LAZY)
    private UploadImage uploadImage;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Like> likes;
    private Integer likeCnt;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Comment> comments;
    private Integer commentCnt;

//    public void update(BoardDto dto){
//        this.title = dto.getTitle();
//        this.body = dto.getBody();
//    }

    public void likeChange(Integer likeCnt){
        this.likeCnt = likeCnt;
    }

    public void commentChange(Integer commentCnt) {
        this.commentCnt = commentCnt;
    }

    public void setUploadImage(UploadImage uploadImage) {
        this.uploadImage = uploadImage;
    }

}
