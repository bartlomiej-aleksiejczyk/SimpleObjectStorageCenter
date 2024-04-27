package com.example.zasobnik.flatbox;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/flatbox")
@RequiredArgsConstructor
public class FlatboxController {
    private FlatboxService flatboxService;

    @PostMapping("/create")
    public ResponseEntity<?> createFlatbox(@RequestParam String slug) {
        Flatbox flatbox = flatboxService.createFlatbox(slug);
        return new ResponseEntity<>(flatbox, HttpStatus.CREATED);
    }

    @PostMapping("/upload/{flatboxId}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long flatboxId) {
        try {
            flatboxService.storeFile(file, flatboxId);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/flatbox/download/")
                    .path(flatboxId.toString())
                    .path("/")
                    .path(file.getOriginalFilename())
                    .toUriString();
            return ResponseEntity.ok(fileDownloadUri);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store file");
        }
    }

    @GetMapping("/download/{flatboxId}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long flatboxId, @PathVariable String filename) {
        try {
            Path filePath = flatboxService.loadFileAsResource(filename, flatboxId);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
