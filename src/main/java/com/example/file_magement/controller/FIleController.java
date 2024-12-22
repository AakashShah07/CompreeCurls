package com.example.file_magement.controller;

import com.example.file_magement.entity.FileMetaData;
import com.example.file_magement.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/file")
public class FIleController {

    @Autowired
    private FileService fileService;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileId = fileService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully. File ID: " + fileId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/downloadOriginal/{fileId}")
    public ResponseEntity<FileSystemResource> downloadOriginalFile(@PathVariable String fileId) {
        try {
            // Decompress the file
            Path decompressedFile = fileService.decompressFile(fileId);

            // Return the decompressed file as a response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + decompressedFile.getFileName() + "\"")
                    .body(new FileSystemResource(decompressedFile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Return a meaningful error response if needed
        }
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String fileId) {
        try {
            Path decryptedCompressedFile = fileService.getDecryptedCompressedFile(fileId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + decryptedCompressedFile.getFileName() + "\"")
                    .body(new FileSystemResource(decryptedCompressedFile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}
