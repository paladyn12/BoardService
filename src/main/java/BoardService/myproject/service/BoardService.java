package BoardService.myproject.service;

import BoardService.myproject.domain.*;
import BoardService.myproject.dto.board.BoardCntDto;
import BoardService.myproject.dto.board.BoardCreateRequest;
import BoardService.myproject.dto.board.BoardDto;
import BoardService.myproject.repository.BoardRepository;
import BoardService.myproject.repository.CommentRepository;
import BoardService.myproject.repository.LikeRepository;
import BoardService.myproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/**
 * getBoardList(BoardCategory, PageRequest, searchType, keyword) : 게시글 조회
    검색 했으면 제목, 닉네임에 키워드를 포함하는 글을 조회
 * getNotice(BoardCategory) : 카테고리의 공지글 조회
 * getBoard(boardId, category) : 게시글 ID와 카테고리로 게시글 정보를 반환
 * writeBoard(BoardCreateRequest, BoardCategory, loginId, auth) : 카테고리와 로그인 ID로 게시글 저장
    자동으로 업로드된 이미지를 서버에 담음
    GREETING 카테고리에 작성 시 로그인 ID와 auth로 등급 향상
 * editBoard(boardId, category, BoardDto) : 게시글 ID와 카테고리 정보로 수정할 게시글 조회
    자동으로 업로드 이미지는 삭제
    수정 사항은 BoardDto를 통해 반영
 * deleteBoard(boardId, category) : 게시글 ID와 카테고리 정보로 삭제할 게시글 조회 후 삭제
    게시글을 작성한 유저의 좋아요 감소 등 side effect 처리
 * getCategory(Long boardId) : 게시글 ID로 카테고리 정보 반환
 * findMyBoard(String category, String loginId) : 카테고리가 board/like/comment 인지에 따라 게시글 조회
 * getBoardCnt() : 게시글 수 조회를 위한 BoardCntDto 반환
 */
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UploadImageService uploadImageService;

    // 검색 했으면 제목, 닉네임에 키워드를 포함하는 글들을 조회, 아니면 전체 조회
    public Page<Board> getBoardList(BoardCategory category, PageRequest pageRequest, String searchType, String keyword){
        if (searchType != null && keyword != null) {
            if (searchType.equals("title")) {
                return boardRepository.findAllByCategoryAndTitleContainsAndUserUserRoleNot(category, keyword, UserRole.ADMIN, pageRequest);
            } else {
                return boardRepository.findAllByCategoryAndUserNicknameContainsAndUserUserRoleNot(category, keyword, UserRole.ADMIN, pageRequest);
            }
        }
        return boardRepository.findAllByCategoryAndUserUserRoleNot(category, UserRole.ADMIN, pageRequest);
    }

    public List<Board> getNotice(BoardCategory category) {
        return boardRepository.findAllByCategoryAndUserUserRole(category, UserRole.ADMIN);
    }

    public BoardDto getBoard(Long boardId, String category) {
        Optional<Board> optBoard = boardRepository.findById(boardId);

        //id에 일치하는 게시글이 없거나 카테고리가 일치하지 않으면 null
        if (optBoard.isEmpty() || !optBoard.get().getCategory().toString().equalsIgnoreCase(category)) {
            return null;
        }
        return BoardDto.of(optBoard.get());
    }

    @Transactional
    public Long writeBoard(BoardCreateRequest req, BoardCategory category, String loginId, Authentication auth) throws IOException {
        User loginUser = userRepository.findByLoginId(loginId).get();

        Board savedBoard = boardRepository.save(req.toEntity(category, loginUser));

        UploadImage uploadImage = uploadImageService.saveImage(req.getUploadImage(), savedBoard);
        if(uploadImage != null) {
            savedBoard.setUploadImage(uploadImage);
        }

        if (category.equals(BoardCategory.GREETING)) {
            loginUser.rankUp(UserRole.SILVER, auth);
        }
        return savedBoard.getId();
    }

    @Transactional
    public Long editBoard(Long boardId, String category, BoardDto dto) throws IOException {
        Optional<Board> optBoard = boardRepository.findById(boardId);

        //id에 일치하는 게시글이 없거나 카테고리가 일치하지 않으면 null
        if (optBoard.isEmpty() || optBoard.get().getCategory().toString().equalsIgnoreCase(category)) {
            return null;
        }

        Board board = optBoard.get();

        //게시글에 이미지가 있었으면 삭제
        if(board.getUploadImage() != null) {
            uploadImageService.deleteImage(board.getUploadImage());
            board.setUploadImage(null);
        }

        UploadImage uploadImage = uploadImageService.saveImage(dto.getNewImage(), board);
        if(uploadImage != null) {
            board.setUploadImage(uploadImage);
        }
        board.update(dto);

        return board.getId();
    }

    public Long deleteBoard(Long boardId, String category) throws IOException {
        Optional<Board> optBoard = boardRepository.findById(boardId);

        //id에 일치하는 게시글이 없거나 카테고리가 일치하지 않으면 null
        if (optBoard.isEmpty() || !optBoard.get().getCategory().toString().equalsIgnoreCase(category)) {
            return null;
        }

        Board board = optBoard.get();

        User boardUser = board.getUser();
        boardUser.likeChange(boardUser.getReceivedLikeCnt() - board.getLikeCnt());
        if (board.getUser() != null) {
            uploadImageService.deleteImage(board.getUploadImage());
        }
        boardRepository.deleteById(boardId);
        return boardId;
    }

    public String getCategory(Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        return board.getCategory().toString().toLowerCase();
    }

    public List<Board> findMyBoard(String category, String loginId) {
        if (category.equals("board")) {
            return boardRepository.findAllByUserLoginId(loginId);
        } else if (category.equals("like")) { // 좋아요가 있는 board들을 찾아 return
            List<Like> likes = likeRepository.findAllByUserLoginId(loginId);
            List<Board> boards = new ArrayList<>();
            for (Like like : likes) {
                boards.add(like.getBoard());
            }
            return boards;
        } else if (category.equals("comment")) { // 댓글이 있는 board들을 찾아 return
            List<Comment> comments = commentRepository.findAllByUserLoginId(loginId);
            List<Board> boards = new ArrayList<>();
            HashSet<Long> commentIds = new HashSet<>();

            for (Comment comment : comments) {
                if (!commentIds.contains(comment.getBoard().getId())) { // 여러 댓글을 적은 경우 중복 방지
                    boards.add(comment.getBoard());
                    commentIds.add(comment.getBoard().getId());
                }
            }
            return boards;
        }
        return null;
    }
    public BoardCntDto getBoardCnt(){
        return BoardCntDto.builder()
                .totalBoardCnt(boardRepository.count())
                .totalNoticeCnt(boardRepository.countAllByUserUserRole(UserRole.ADMIN))
                .totalGreetingCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.GREETING, UserRole.ADMIN))
                .totalFreeCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.FREE, UserRole.ADMIN))
                .totalGoldCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.GOLD, UserRole.ADMIN))
                .build();
    }
}
