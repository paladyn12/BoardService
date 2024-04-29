package BoardService.myproject.controller;

import BoardService.myproject.repository.BoardRepository;
import BoardService.myproject.repository.UserRepository;
import BoardService.myproject.service.BoardService;
import BoardService.myproject.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/likes")
@RequiredArgsConstructor
// comment와 달리 메세지를 출력하지 않음. redirect로 이동
public class LikeController {

    private final LikeService likeService;
    private final BoardService boardService;

    @GetMapping("/add/{boardId}")
    public String addLike(@PathVariable Long boardId, Authentication auth) {
        likeService.addLike(auth.getName(), boardId);
        return "redirect:/boards/" + boardService.getCategory(boardId) + "/" + boardId;
    }

    @GetMapping("/delete/{boardId}")
    public String deleteLike(@PathVariable Long boardId, Authentication auth) {
        likeService.deleteLike(auth.getName(), boardId);
        return "redirect:/boards/" + boardService.getCategory(boardId) + "/" + boardId;
    }
}
