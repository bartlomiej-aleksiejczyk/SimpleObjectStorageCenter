package com.example.zasobnik.flatbox;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public String storeFile(MultipartFile file, String flatboxSlug) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String sanitizedFilename = sanitizeFilename(originalFilename);
        Path targetLocation = Paths.get(storageDirectoryPath, flatboxSlug);
        Files.createDirectories(targetLocation);
        Path filePath = ensureUniqueFilename(targetLocation, sanitizedFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/flatbox/download/")
                .path(flatboxSlug)
                .path("/")
                .path(filePath.getFileName().toString())
                .toUriString();

        return fileDownloadUri;
    }

    @Transactional
    public void removeFile(String filename, String flatboxSlug) throws IOException {
        Path filePath = Paths.get(storageDirectoryPath, flatboxSlug, filename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new FileNotFoundException("File not found");
        }
    }

    // TODO: ADD pagination here
    public List<String> listFiles(String flatboxSlug) {
        Path directoryPath = Paths.get(storageDirectoryPath, flatboxSlug);
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files", e);
        }
    }

    public Path loadFileAsResource(String filename, String flatboxSlug) {
        return Paths.get(storageDirectoryPath, flatboxSlug, filename);
    }

    private String sanitizeFilename(String filename) {
        String baseName = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);
        String sanitized = baseName.toLowerCase().replaceAll("[^a-z0-9\\-]", "-");
        return sanitized + (extension.isEmpty() ? "" : "." + extension.toLowerCase());
    }

    private Path ensureUniqueFilename(Path directory, String sanitizedFilename) throws IOException {
        Path file = directory.resolve(sanitizedFilename);
        int count = 0;
        while (Files.exists(file)) {
            count++;
            String newName = FilenameUtils.getBaseName(sanitizedFilename) + "-" + count +
                    (FilenameUtils.getExtension(sanitizedFilename).isEmpty() ? ""
                            : "." + FilenameUtils.getExtension(sanitizedFilename));
            file = directory.resolve(newName);
        }
        return file;
    }
}
