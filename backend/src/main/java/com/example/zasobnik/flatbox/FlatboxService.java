package com.example.zasobnik.flatbox;

import lombok.RequiredArgsConstructor;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.zasobnik.flatbox.exceptions.FileListException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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
    private static final int MAX_FLATBOX_LENGTH = 63;

    private final FlatboxRepository flatBoxRepository;

    @Transactional
    public Flatbox createFlatbox(String slug) {
        if (!isValidSlug(slug)) {
            throw new IllegalArgumentException("Invalid slug provided.");
        }
        if (flatBoxRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Slug already in use.");
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

    public Resource prepareFileResource(String filename, String flatboxSlug)
            throws MalformedURLException, FileNotFoundException {
        Path file = Paths.get(storageDirectoryPath, flatboxSlug, filename);
        if (!Files.exists(file)) {
            throw new FileNotFoundException("File not found");
        }

        Resource resource = new UrlResource(file.toUri());
        return resource;
    }

    public String determineContentDisposition(String contentType, boolean isPreviewEligible) {
        boolean isRenderable = isPreviewEligible && DownloadFileUtils.isRenderable(contentType);
        return isRenderable ? "inline" : "attachment";
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

    public static String sanitizeFilename(String filename) {
        String baseName = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);

        String sanitizedBaseName = baseName.toLowerCase().replaceAll("[^a-z0-9\\.\\-]", "-");

        sanitizedBaseName = sanitizedBaseName.replaceAll("^xn--|^-+|-+$|\\.+", "-");

        while (sanitizedBaseName.contains("..")) {
            sanitizedBaseName = sanitizedBaseName.replace("..", ".");
        }

        if (sanitizedBaseName.length() > MAX_FILENAME_LENGTH) {
            sanitizedBaseName = sanitizedBaseName.substring(0, MAX_FILENAME_LENGTH).replaceAll("-+$|\\.+$", "");
        }

        while (sanitizedBaseName.length() < MIN_FILENAME_LENGTH) {
            sanitizedBaseName += "-pad";
            if (sanitizedBaseName.length() > MAX_FILENAME_LENGTH) {
                sanitizedBaseName = sanitizedBaseName.substring(0, MAX_FILENAME_LENGTH).replaceAll("-+$|\\.+$", "");
            }
        }

        if (sanitizedBaseName.matches("(\\d+\\.){3}\\d+")) {
            sanitizedBaseName = sanitizedBaseName.replace(".", "-");
        }

        return sanitizedBaseName + (extension.isEmpty() ? "" : "." + extension);
    }

    public static Path ensureUniqueFilename(Path directory, String sanitizedFilename) throws IOException {
        Path file = directory.resolve(sanitizedFilename);
        int count = 1;
        while (Files.exists(file)) {
            String baseName = FilenameUtils.getBaseName(sanitizedFilename);
            String extension = getFullExtension(sanitizedFilename); // Use a custom method to handle compound extensions

            String newName = baseName + "-" + count;
            int extensionLength = extension.isEmpty() ? 0 : extension.length() + 1; // +1 for the dot
            if (newName.length() > MAX_FILENAME_LENGTH - extensionLength) {
                newName = newName.substring(0, MAX_FILENAME_LENGTH - extensionLength).replaceAll("-+$", "");
            }

            newName += (extension.isEmpty() ? "" : "." + extension);
            file = directory.resolve(newName);
            count++;
        }
        return file;
    }

    private static String getFullExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        int lastSepIndex = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
        if (lastDotIndex > lastSepIndex) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }

}
