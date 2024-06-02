package michael.m.marketProject.custom_beans;

import lombok.RequiredArgsConstructor;
import michael.m.marketProject.service.file_storage_service.FileStorageService;

import java.util.List;

@RequiredArgsConstructor
public class HandleOldPicturesDeletionOnEntityChange {
    private final FileStorageService fileStorageService;

    public void deleteOldImage(String oldPicture){
        fileStorageService.deleteFile(oldPicture);
    }

    public void deleteOldImages(List<String> oldPictures){
        for (String oldPicture : oldPictures) {
            deleteOldImage(oldPicture);
        }
    }
}
