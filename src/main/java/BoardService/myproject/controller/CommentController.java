package BoardService.myproject.controller;

import BoardService.myproject.domain.dto.comment.CommentCreateRequest;
import BoardService.myproject.service.BoardService;
import BoardService.myproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

    @PostMapping("/{boardId}")
    public String addComments(@PathVariable Long boardId, @ModelAttribute CommentCreateRequest req,
                              Authentication auth, Model model) {
        commentService.writeComment(boardId, req, auth.getName());

        model.addAttribute("message", "댓글이 추가되었습니다.");
        model.addAttribute("nextUrl", "/boards/" + boardService.getCategory(boardId) + "/" + boardId);
        return "printMessage";
    }

    @PostMapping("/{commentId}/edit")
    public String editComment(@PathVariable Long commentId, @ModelAttribute CommentCreateRequest req,
                              Authentication auth, Model model) {
        Long editedCommentId = commentService.editComment(commentId, req.getBody(), auth.getName());
        model.addAttribute("message", editedCommentId == null? "잘못된 요청입니다." : "댓글이 수정 되었습니다.");
        model.addAttribute("nextUrl", "/boards/" + boardService.getCategory(editedCommentId) + "/" + editedCommentId);
        return "printMessage";
    }

    @GetMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, Authentication auth, Model model) {
        Long deletedCommentId = commentService.deleteComment(commentId, auth.getName());
        model.addAttribute("message", deletedCommentId == null? "작성자만 삭제 가능합니다." : "댓글이 삭제되었습니다.");
        model.addAttribute("nextUrl", "/boards/" + boardService.getCategory(deletedCommentId) + "/" + deletedCommentId);
        return "printMessage";
    }

}
