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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/flatbox")
@Tag(name = "Flatbox Controller", description = "Endpoints connected to flatbox and its content manipulation.")
@RequiredArgsConstructor
public class FlatboxController {
    private final FlatboxService flatboxService;

    @Operation(summary = "Create new flatbox object to aggregate files")
    @PostMapping("/create")
    public ResponseEntity<?> createFlatbox(@RequestParam String slug) {
        Flatbox flatbox = flatboxService.createFlatbox(slug);
        return new ResponseEntity<>(flatbox, HttpStatus.CREATED);
    }

    @Operation(summary = "Upload file to given flatbox")
    @PostMapping(value = "/upload/{flatboxId}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart(name = "file") MultipartFile file,
            @PathVariable Long flatboxId) {
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

    @Operation(summary = "Download file from given flatbox")
    @GetMapping("/download/{flatboxId}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String flatboxSlug, @PathVariable String filename) {
        try {
            Path filePath = flatboxService.loadFileAsResource(filename, flatboxSlug);
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
