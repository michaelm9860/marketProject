package michael.m.marketProject.controller;

import lombok.RequiredArgsConstructor;
import michael.m.marketProject.service.file_storage_service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
