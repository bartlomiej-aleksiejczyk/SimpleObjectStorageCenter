package com.example.zasobnik.flatbox;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.zasobnik.flatbox.exceptions.FileListException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipOutputStream;

//TODO: Add edit endpoitn (shorthnad for remove and add)
@RestController
@RequestMapping("/api/client/v1/flatbox")
@Tag(name = "Flatbox Controller", description = "Endpoints connected to flatbox and its content manipulation. Intended for server-to-browser communication.")
@RequiredArgsConstructor
public class FlatboxClientController {
    private final FlatboxService flatboxService;

    static final Logger log = LoggerFactory.getLogger(FlatboxClientController.class);

    @Operation(summary = "Create new flatbox object to aggregate files")
    @PostMapping("/create")
    public ResponseEntity<?> createFlatbox(@Valid @RequestBody FlatboxCreateDTO flatboxCreateDTO) {
        Flatbox flatbox = flatboxService.createFlatbox(flatboxCreateDTO);
        return new ResponseEntity<>(flatbox, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload/{flatboxSlug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(HttpServletRequest request,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @PathVariable String flatboxSlug) {
        if (!request.getContentType().startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return ResponseEntity.badRequest()
                    .body("Invalid request type: Content-Type must be 'multipart/form-data'.");
        }
        if (file == null) {
            return ResponseEntity.badRequest().body("No file provided. Please attach a file to the request.");
        }

        try {
            String fileDownloadUri = flatboxService.storeFile(file, flatboxSlug);
            return ResponseEntity.ok(fileDownloadUri);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store file");
        }
    }

    // TODO: Extract logic to service
    // TODO: Chech if it is worth to implement csp
    @GetMapping("/download/{flatboxSlug}/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename,
            @PathVariable String flatboxSlug,
            @RequestParam(defaultValue = "true") boolean isPreviewEligible) {
        try {
            Resource resource = flatboxService.prepareFileResource(filename, flatboxSlug);
            String contentType = DownloadFileUtils.determineContentType(Paths.get(filename));
            String disposition = flatboxService.determineContentDisposition(contentType, isPreviewEligible);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            disposition + "; filename=\"" + resource.getFilename() + "\"")
                    .header("X-Content-Type-Options", "nosniff")
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "Remove file from given flatbox")
    @DeleteMapping("/remove/{flatboxType}/{flatboxSlug}/{filename:.+}")
    public ResponseEntity<?> removeFile(@PathVariable String flatboxType, @PathVariable String flatboxSlug,
            @PathVariable String filename) {
        try {
            flatboxService.removeFile(flatboxType, filename, flatboxSlug);
            return ResponseEntity.ok().body("File removed successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove file");
        }
    }

    // TODO: Check if checked exception is needed here
    @GetMapping("/list/{flatboxSlug}")
    public ResponseEntity<Page<String>> listFiles(@PathVariable String flatboxSlug,
            @PageableDefault(size = 10, maxPageSize = 1000) Pageable pageable) {
        Page<String> filePage = flatboxService.listFiles(flatboxSuch, pageable);
        return ResponseEntity.ok(filePage);
    }

    @Operation(summary = "Download given directory as zip by streaming")
    @GetMapping("/zip-and-download/{flatboxSlug}")
    public void zipAndDownload(@PathVariable String flatboxSlug, HttpServletResponse response) {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + flatboxSlug + ".zip\"");

        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
            flatboxService.zipDirectory(flatboxSlug, zos);
            zos.finish();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to process file: " + ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>("Invalid pagination parameters: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

}
