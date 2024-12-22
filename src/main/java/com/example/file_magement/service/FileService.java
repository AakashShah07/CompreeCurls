package com.example.file_magement.service;

import com.example.file_magement.entity.FileMetaData;
import com.example.file_magement.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private CompressionService service;

    public String uploadFile(MultipartFile file) throws IOException {
        // Save file to temporary storage
        Path tempFile = Files.createTempFile("upload_", file.getOriginalFilename());
        file.transferTo(tempFile);

        // Compress the file
        Path compressedFile = service.compressFile(tempFile);

        // Save metadata to database
        FileMetaData metadata = new FileMetaData();
        metadata.setFilename(file.getOriginalFilename());
        metadata.setFilePath(compressedFile.toString());
        metadata.setFileSize(file.getSize());
        metadata.setStatus("Compressed");
        fileRepository.save(metadata); // MongoDB will generate the ID

        // Return the generated ID from MongoDB (it will be set after save)
        return metadata.getId(); // No arguments here, just the getter
    }

    public FileMetaData getFile(String fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found!"));
    }
    public Path decompressFile(String fileId) throws IOException {
        // Retrieve metadata from the database
        FileMetaData metadata = getFile(fileId);

        // Path to the compressed file
        Path compressedFilePath = Path.of(metadata.getFilePath());

        // Decompress the file
        return service.decompressFile(compressedFilePath);
    }

    public Path getDecryptedCompressedFile(String fileId) throws IOException {
        FileMetaData metadata = getFile(fileId); // Fetch metadata
        Path encryptedFilePath = Path.of(metadata.getFilePath()); // Path to encrypted file

        // Decrypt the file, keeping it compressed
        return service.decryptFile(encryptedFilePath);
    }



}
