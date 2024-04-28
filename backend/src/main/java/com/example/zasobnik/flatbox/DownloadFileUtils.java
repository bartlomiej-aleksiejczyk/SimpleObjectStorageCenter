package com.example.zasobnik.flatbox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadFileUtils {
    public static String determineContentType(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        try {
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null || mimeType.trim().isEmpty()) {
                return "application/octet-stream";
            }
            return mimeType;
        } catch (IOException e) {
            System.err.println("Error determining MIME type: " + e.getMessage());
            return "application/octet-stream";
        }
    }

    public static boolean isRenderable(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            return false;
        }
        return mimeType.startsWith("image/")
                || mimeType.startsWith("text/")
                || mimeType.startsWith("audio/")
                || mimeType.startsWith("video/")
                || mimeType.equals("application/pdf")
                || mimeType.equals("application/json")
                || mimeType.equals("application/xml")
                || mimeType.equals("application/xhtml+xml")
                || mimeType.equals("application/javascript")
                || mimeType.equals("font/woff")
                || mimeType.equals("font/woff2");
    }

}
