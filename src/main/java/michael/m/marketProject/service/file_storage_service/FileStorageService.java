package michael.m.marketProject.service.file_storage_service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import michael.m.marketProject.config.FileStorageProperties;
import michael.m.marketProject.error.FileStorageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;
    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        // Check if the file is empty or if the file name is null
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new FileStorageException("File is empty or filename is null");
        }

        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Pattern to match invalid characters for file names
        String invalidCharsPattern = ".*[<>:\"/\\\\|?*].*";

        try {
            // Check if the file's name contains invalid characters or path sequence
            if (originalFileName.contains("..") || Pattern.matches(invalidCharsPattern, originalFileName)) {
                throw new FileStorageException("Filename contains invalid path sequence or characters: " + originalFileName);
            }

            // Generate a unique file name
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = UUID.randomUUID() + fileExtension;

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found " + fileName);
            }
        } catch (IOException ex) {
            throw new FileStorageException("File not found " + fileName, ex);
        }
    }

    private String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i);
        }
        return extension;
    }
}