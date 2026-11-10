package com.evalforge.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.evalforge.storage.service.StorageService;
@SpringBootTest
public class S3StorageTest {
    @Autowired
    private StorageService storageService;

    @Test
    void uploadDownloadDelete() throws Exception {
        Path file = Files.createTempFile("s3-test", ".txt");
        Files.writeString(file, "hello seaweedfs");

        String bucket = "evalforge-benchmarks";
        String key = "test/hello.txt";

        storageService.uploadFile(bucket, key, file, "text/plain");
        assertTrue(storageService.exists(bucket, key));

        try (InputStream in = storageService.download(bucket, key)) {
            assertEquals("hello seaweedfs",
                    new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }

        storageService.delete(bucket, key);
        assertFalse(storageService.exists(bucket, key));
    }
}
