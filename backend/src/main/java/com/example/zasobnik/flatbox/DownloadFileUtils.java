package com.example.zasobnik.flatbox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadFileUtils {
    public static String determineContentType(Path filePath) {
        try {
            String mimeType = Files.probeContentType(filePath);
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    public static boolean isRenderable(String mimeType) {
        return mimeType.startsWith("image/") || mimeType.equals("application/pdf");
    }
}
