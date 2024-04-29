package BoardService.myproject.service;

import BoardService.myproject.domain.Board;
import BoardService.myproject.domain.UploadImage;
import BoardService.myproject.repository.BoardRepository;
import BoardService.myproject.repository.UploadImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@Service
// saveImage() : 입력된 파일의 이름을 "UUID + 원본 파일 확장자" 로 바꿔 저장
// downloadImage() : 다시 원본 파일명으로 수정 후 파일 return
public class UploadImageService {

    private final UploadImageRepository uploadImageRepository;
    private final BoardRepository boardRepository;
    private final String rootPath = System.getProperty("user.dir");
    private final String fileDir = rootPath + "/src/main/resources/static/upload-images/";

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public UploadImage saveImage(MultipartFile multipartFile, Board board) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        // 원본 파일명으로 서버에 저장되는 파일명을 만듦
        // 파일명이 중복되지 않도록 UUID로 설정 및 확장자 유지
        String savedFilename = UUID.randomUUID() + "." + extractExt(originalFilename);

        // 파일 저장
        multipartFile.transferTo(new File(getFullPath(savedFilename)));

        return uploadImageRepository.save(UploadImage.builder()
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .board(board)
                .build());

    }

    @Transactional
    public void deleteImage(UploadImage uploadImage) throws IOException {
        uploadImageRepository.delete(uploadImage);
        Files.deleteIfExists(Paths.get(getFullPath(uploadImage.getSavedFilename())));
    }

    // 확장자 추출 메서드
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }


    public ResponseEntity<UrlResource> downloadImage(Long boardId) throws MalformedURLException {
        //boardId에 해당하는 게시글이 없으면 null return
        Board board = boardRepository.findById(boardId).get();
        if (board == null || board.getUploadImage() == null) {
            return null;
        }

        UrlResource urlResource = new UrlResource("file:" + getFullPath(board.getUploadImage().getSavedFilename()));

        // 업로드 한 파일명이 한글인 경우를 대비한 작업
        String encodedUploadFileName = UriUtils
    }
}
