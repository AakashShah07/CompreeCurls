package com.example.file_magement.service;

import lzma.streams.LzmaInputStream;
import lzma.streams.LzmaOutputStream;
import org.springframework.stereotype.Service;
import lzma.sdk.lzma.Decoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CompressionService {

    public Path compressFile(Path inputFile) throws IOException {
        // Validate input file
        if (!Files.exists(inputFile) || !Files.isRegularFile(inputFile)) {
            throw new IOException("Input file does not exist or is not a regular file: " + inputFile);
        }

        // Create a temporary file for the compressed output
        Path compressedFilePath = Files.createTempFile("compressed_", ".lzma");

        try (
                // Create an LZMA output stream
                LzmaOutputStream outputStream = new LzmaOutputStream.Builder(
                        new FileOutputStream(compressedFilePath.toFile()))
                        .useMaximalDictionarySize()
                        .useMaximalFastBytes()
                        .build();
                InputStream inputStream = new FileInputStream(inputFile.toFile())
        ) {
            byte[] buffer = new byte[8192]; // Larger buffer for performance
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return compressedFilePath;
    }


    public Path decompressFile(Path compressedFile) throws IOException {
        // Validate compressed file
        if (!Files.exists(compressedFile) || !Files.isRegularFile(compressedFile)) {
            throw new IOException("Compressed file does not exist or is not a regular file: " + compressedFile);
        }

        // Create a temporary file for the decompressed output
        Path decompressedFile = Files.createTempFile("decompressed_", ".txt");

        try (
                // Create an LZMA input stream with the required Decoder
                InputStream fileInputStream = new FileInputStream(compressedFile.toFile());
                LzmaInputStream lzmaInputStream = new LzmaInputStream(fileInputStream, new Decoder());
                OutputStream fileOutputStream = new FileOutputStream(decompressedFile.toFile())
        ) {
            byte[] buffer = new byte[8192]; // Larger buffer for performance
            int bytesRead;
            while ((bytesRead = lzmaInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }

        return decompressedFile;
    }

    public Path decryptFile(Path encryptedFile) throws IOException {
        // Validate encrypted file
        if (!Files.exists(encryptedFile) || !Files.isRegularFile(encryptedFile)) {
            throw new IOException("Encrypted file does not exist or is not a regular file: " + encryptedFile);
        }

        // Create a temporary file for the decrypted (still compressed) output
        Path decryptedFile = Files.createTempFile("decrypted_", ".lzma");

        try (
                InputStream encryptedInputStream = new FileInputStream(encryptedFile.toFile());
                OutputStream decryptedOutputStream = new FileOutputStream(decryptedFile.toFile())
        ) {
            // Simulate decryption (replace with your actual decryption logic)
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = encryptedInputStream.read(buffer)) != -1) {
                decryptedOutputStream.write(buffer, 0, bytesRead);
            }
        }

        return decryptedFile;
    }


}
