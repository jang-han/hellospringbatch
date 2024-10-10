package com.example.demo.tasklet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.example.demo.config.AppConfig;

class DeleteOldFilesTaskletTest {

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private DeleteOldFilesTasklet deleteOldFilesTasklet;

    @Mock
    private StepContribution contribution;

    @Mock
    private ChunkContext chunkContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mocking the directory paths
        when(appConfig.getCsvOutputOk()).thenReturn("mocked/ok");
        when(appConfig.getCsvOutputNg()).thenReturn("mocked/ng");
        when(appConfig.getCsvBackup()).thenReturn("mocked/backup");
    }

    @Test
    void testExecute_DeletesOldFiles() throws Exception {
        // Given
        createMockedFiles(); // 파일 생성

        // Set the last modified time to one year ago
        setLastModifiedToOneYearAgo(Path.of("mocked/ok/oldFile.csv"));
        setLastModifiedToOneYearAgo(Path.of("mocked/ng/oldFile.csv"));

        // When
        deleteOldFilesTasklet.execute(contribution, chunkContext); // 삭제 작업 실행

        // Then
        assertFalse(Files.exists(Path.of("mocked/ok/oldFile.csv")), "OKファイルが削除されるべきです。");
        assertFalse(Files.exists(Path.of("mocked/ng/oldFile.csv")), "NGファイルが削除されるべきです。");
    }

    private void createMockedFiles() throws IOException {
        // Create directories if they don't exist
        Files.createDirectories(Path.of("mocked/ok"));
        Files.createDirectories(Path.of("mocked/ng"));

        // Create the old file with the specific name
        Path oldFilePathOk = Path.of("mocked/ok/oldFile.csv");
        Path oldFilePathNg = Path.of("mocked/ng/oldFile.csv");

        // Create the files if they do not exist
        if (!Files.exists(oldFilePathOk)) {
            Files.createFile(oldFilePathOk); // Create the old file in OK directory
        }
        if (!Files.exists(oldFilePathNg)) {
            Files.createFile(oldFilePathNg); // Create the old file in NG directory
        }
    }

    private void setLastModifiedToOneYearAgo(Path filePath) throws IOException {
        Instant oneYearAgo = Instant.now().minus(366, ChronoUnit.DAYS); // 1년 이상 전에 설정
        Files.setLastModifiedTime(filePath, FileTime.from(oneYearAgo));
    }
}
