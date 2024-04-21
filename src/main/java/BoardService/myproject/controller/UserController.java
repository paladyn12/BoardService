package BoardService.myproject.controller;

import BoardService.myproject.domain.User;
import BoardService.myproject.dto.UserDto;
import BoardService.myproject.dto.UserJoinRequest;
import BoardService.myproject.dto.UserLoginRequest;
import BoardService.myproject.service.BoardService;
import BoardService.myproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BoardService boardService;

    @GetMapping("/join")
    public String joinPage(Model model){
        model.addAttribute("userJoinRequest", new UserJoinRequest());
        return "users/join";
    }
    @PostMapping("join")
    public String join(@Valid @ModelAttribute UserJoinRequest req, BindingResult bindingResult, Model model){
        if (userService.joinVaild(req, bindingResult).hasErrors()){
            return "users/join";
        }
        userService.join(req);
        model.addAttribute("message", "회원가입에 성공했습니다!\n로그인 후 사용 가능합니다.");
        model.addAttribute("nextUrl", "/users/login");
        return "printMessage";
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request){


        //로그인 성공 시 이전 페이지로 redirect되게 하는 세션 저장
        String uri = request.getHeader("Referer"); //클라이언트의 이전 페이지 uri
        //로그인 페이지나 회원가입 페이지에선 이전 페이지로 저장하지 않도록 함
        if(uri != null && !uri.contains("/login") && !uri.contains("/join")){
            //이전 페이지 uri를 세션에 prevPage라는 이름으로 저장하여 접근할 수 있도록 함
            request.getSession().setAttribute("prevPage", uri);
        } //즉 이 코드는 로그인 및 회원가입 후 이전 페이지로 돌아갈 수 있도록 하는 코드

        model.addAttribute("userLoginRequest", new UserLoginRequest());
        return "users/login";
    }

    @GetMapping("/myPage/{category}")
    public String myPage(@PathVariable String category, Authentication auth, Model model){
        //Authentication 객체의 정보는 Spring Security에 의해 자동 주입
        model.addAttribute("boards", boardService.findMyBoard(category, auth.getName()));
        model.addAttribute("category", category);
        model.addAttribute("user", userService.myInfo(auth.getName()));

        return "users/myPage";
    }

    @GetMapping("/edit")
    public String userEditPage(Authentication auth, Model model){
        User user = userService.myInfo(auth.getName());
        model.addAttribute("userDto", UserDto.of(user));
        return "users/edit";
    }
    @PostMapping("/edit")
    public String userEdit(@Valid @ModelAttribute UserDto dto, BindingResult bindingResult,
                           Authentication auth, Model model){
        
        if(userService.editValid(dto, bindingResult, auth.getName()).hasErrors()){
            return "users/edit";
        }

        userService.edit(dto, auth.getName());

        model.addAttribute("message", "정보가 수정되었습니다.");
        model.addAttribute("nextUrl", "/users/myPage/board");
        return "printMessage";
    }

    @GetMapping("/delete")
    public String userDeletePage(Authentication auth, Model model){
        User user = userService.myInfo(auth.getName());
        model.addAttribute("userDto", UserDto.of(user));
        return "users/delete";
    }
    @PostMapping("/delete")
    public String userDelete(@ModelAttribute UserDto dto, Authentication auth, Model model){
        Boolean deleteSuccess = userService.delete(auth.getName(), dto.getNowPassword());
        if(deleteSuccess){
            model.addAttribute("message", "탈퇴 되셨습니다.");
            model.addAttribute("nextUrl", "/users/logout");
            return "printMessage";
        } else {
            model.addAttribute("message", "현재 비밀번호가 틀려 탈퇴에 실패하셨습니다.");
            model.addAttribute("nextUrl", "users/delete");
            return "printMessage";
        }
    }

    @GetMapping("/admin")
    public String adminPage(@RequestParam(required = false, defaultValue = "1") int page,
                            @RequestParam(required = false, defaultValue = "") String keyword,
                            Model model){
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());
        Page<User> users = userService.findAllByNickname(keyword, pageRequest);

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "users/admin";
    }

    @GetMapping("/admin/{userId}")
    public String adminChangeRole(@PathVariable Long userId,
                                  @RequestParam(required = false, defaultValue = "1") int page,
                                  @RequestParam(required = false, defaultValue = "") String keyword) throws UnsupportedEncodingException {

        userService.changeRole(userId);
        return "redirect:/users/admin?page="+page+"&keyword="+ URLEncoder.encode(keyword, "UTF-8");
    }
}
