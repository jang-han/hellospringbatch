package com.example.demo.tasklet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import com.example.demo.config.AppConfig;

class CsvFileCheckAndMoveTaskletTest {

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private CsvFileCheckAndMoveTasklet csvFileCheckAndMoveTasklet;

    @Mock
    private StepContribution contribution;

    @Mock
    private ChunkContext chunkContext;

    @Mock
    private StepContext stepContext;  // StepContext 모킹 추가

    @Mock
    private StepExecution stepExecution;  // StepExecution 모킹 추가

    @Mock
    private JobExecution jobExecution;  // JobExecution 모킹 추가

    @Mock
    private ExecutionContext executionContext;  // ExecutionContext 모킹 추가

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mocking the directory paths
        when(appConfig.getCsvInput()).thenReturn("mocked/input");
        when(appConfig.getCsvBackup()).thenReturn("mocked/backup");

        // Mocking the ChunkContext, StepContext, StepExecution, JobExecution and ExecutionContext
        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getStepExecution()).thenReturn(stepExecution);
        when(stepExecution.getJobExecution()).thenReturn(jobExecution);  // JobExecution 반환 설정
        when(jobExecution.getExecutionContext()).thenReturn(executionContext); // ExecutionContext 반환 설정
    }

    @Test
    void testExecute_MovesCsvFiles() throws Exception {
        // Given
        createMockedCsvInput(); // 파일 생성

        // When
        csvFileCheckAndMoveTasklet.execute(contribution, chunkContext); // 이동 작업 실행

        // Then
        assertTrue(Files.exists(Path.of("mocked/backup/testFile.csv")), "CSVファイルがバックアップに移動されるべきです。");
        assertFalse(Files.exists(Path.of("mocked/input/testFile.csv")), "CSVファイルは入力フォルダーから削除されるべきです。");
    }

    private void createMockedCsvInput() throws IOException {
        // Create CSV input file with initial data
        Path csvFilePath = Path.of("mocked/input/testFile.csv");

        // Delete the file if it already exists
        if (Files.exists(csvFilePath)) {
            Files.delete(csvFilePath);
        }

        // Create the CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath.toFile()))) {
            writer.write("名前,メール,身長,体重\n");
            writer.write("山田花子,hanako@example.com,171.5,66.2\n");
        }
    }
}
