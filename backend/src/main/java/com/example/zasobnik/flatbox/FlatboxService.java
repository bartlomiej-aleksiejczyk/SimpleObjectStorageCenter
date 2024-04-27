package com.example.zasobnik.flatbox;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FlatboxService {

    @Value("${STORAGE_DIRECTORY_PATH}")
    private static String storageDirectoryPath = "./flatboxs/";

    private final FlatboxRepository flatBoxRepository;

    @Transactional
    public Flatbox createFlatbox(String slug) {
        Flatbox flatbox = new Flatbox();
        flatbox.setSlug(slug);
        flatbox.setAccessType(FlatboxAccessType.PUBLIC);
        return flatBoxRepository.save(flatbox);
    }

    @Transactional
    public void storeFile(MultipartFile file, Long flatBoxId) throws IOException {
        Path targetLocation = Paths.get(storageDirectoryPath, flatBoxId.toString());
        Files.createDirectories(targetLocation);
        Files.copy(file.getInputStream(), targetLocation.resolve(file.getOriginalFilename()));
    }

    public Path loadFileAsResource(String filename, String flatboxSlug) {
        return Paths.get(storageDirectoryPath, flatboxSlug, filename);
    }
}
