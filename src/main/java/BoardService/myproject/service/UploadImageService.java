package BoardService.myproject.service;

import BoardService.myproject.domain.Board;
import BoardService.myproject.domain.UploadImage;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadImageService {
    public UploadImage saveImage(MultipartFile uploadImage, Board savedBoard) {
    }

    public void deleteImage(UploadImage uploadImage) {
    }

    public String getFullPath(String filename) {
    }

    public ResponseEntity<UrlResource> downloadImage(Long boardId) {
    }
}
