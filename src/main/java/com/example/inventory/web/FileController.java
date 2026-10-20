package com.example.inventory.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final String uploadDir;

    public FileController(@Value("${inventory.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /**
     * VULNERABLE: path traversal, e.g. {@code ?name=../../../../etc/passwd}.
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String name) throws IOException {
        File file = new File(uploadDir, name);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(Files.readAllBytes(file.toPath()));
    }

    /**
     * VULNERABLE: path traversal via the client-supplied original filename.
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) throws IOException {
        File target = new File(uploadDir, file.getOriginalFilename());
        target.getParentFile().mkdirs();
        file.transferTo(target);
        return Map.of("stored", target.getAbsolutePath(), "size", file.getSize());
    }
}
