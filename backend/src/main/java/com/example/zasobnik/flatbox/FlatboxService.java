package com.example.zasobnik.flatbox;

import lombok.RequiredArgsConstructor;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.zasobnik.flatbox.exceptions.FileListException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FlatboxService {

    @Value("${STORAGE_DIRECTORY_PATH}")
    private static String storageDirectoryPath = "./flatboxs/";

    private static final int MIN_FILENAME_LENGTH = 3;
    private static final int MAX_FILENAME_LENGTH = 200;

    private static final int MIN_FLATBOX_LENGTH = 3;
    private static final int MAX_FLATBOX_LENGTH = 200;

    private final FlatboxRepository flatBoxRepository;

    @Transactional
    public Flatbox createFlatbox(String slug) {
        if (!isValidSlug(slug)) {
            throw new IllegalArgumentException("Invalid slug provided.");
        }

        Flatbox flatbox = new Flatbox();
        flatbox.setSlug(slug);
        flatbox.setAccessType(FlatboxAccessType.PUBLIC);
        return flatBoxRepository.save(flatbox);
    }

    private boolean isValidSlug(String slug) {
        return slug != null
                && slug.length() >= MIN_FLATBOX_LENGTH
                && slug.length() <= MAX_FLATBOX_LENGTH
                && slug.matches("^(?!xn--)[a-z0-9]+(-[a-z0-9]+)*$");
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
    public List<String> listFiles(String flatboxSlug) throws FileListException {
        Path directoryPath = Paths.get(storageDirectoryPath, flatboxSlug);
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileListException("Failed to list files", e);
        }
    }

    public Path loadFileAsResource(String filename, String flatboxSlug) {
        return Paths.get(storageDirectoryPath, flatboxSlug, filename);
    }

    private String sanitizeFilename(String filename) {
        String baseName = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);

        String sanitizedBaseName = baseName.toLowerCase().replaceAll("[^a-z0-9\\-]", "-");

        sanitizedBaseName = sanitizedBaseName.replaceAll("^xn--|^-+|-+$", "");

        if (sanitizedBaseName.length() > MAX_FILENAME_LENGTH) {
            sanitizedBaseName = sanitizedBaseName.substring(0, MAX_FILENAME_LENGTH).replaceAll("-+$", "");
        }

        while (sanitizedBaseName.length() < MIN_FILENAME_LENGTH) {
            sanitizedBaseName += "-pad";
            if (sanitizedBaseName.length() > MAX_FILENAME_LENGTH) {
                sanitizedBaseName = sanitizedBaseName.substring(0, MAX_FILENAME_LENGTH).replaceAll("-+$", "");
            }
        }

        return sanitizedBaseName + (extension.isEmpty() ? "" : "." + extension);
    }

    private Path ensureUniqueFilename(Path directory, String sanitizedFilename) throws IOException {
        Path file = directory.resolve(sanitizedFilename);
        int count = 1;
        while (Files.exists(file)) {
            String baseName = FilenameUtils.getBaseName(sanitizedFilename);
            String extension = FilenameUtils.getExtension(sanitizedFilename);
            String newName = baseName + "-" + count;

            if (newName.length() > MAX_FILENAME_LENGTH - (extension.isEmpty() ? 0 : extension.length() + 1)) {
                newName = newName.substring(0, MAX_FILENAME_LENGTH - (extension.isEmpty() ? 0 : extension.length() + 1))
                        .replaceAll("-+$", "");
            }

            newName += (extension.isEmpty() ? "" : "." + extension);
            file = directory.resolve(newName);
            count++;
        }
        return file;
    }

}
