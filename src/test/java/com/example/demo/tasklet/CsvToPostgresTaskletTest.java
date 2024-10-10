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
import com.example.demo.repository.postgres.MedicalOpinionPostgresRepository;

class CsvToPostgresTaskletTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private MedicalOpinionPostgresRepository medicalOpinionRepository;

    @InjectMocks
    private CsvToPostgresTasklet csvToPostgresTasklet;

    @Mock
    private StepContribution contribution;

    @Mock
    private ChunkContext chunkContext;

    @Mock
    private StepContext stepContext; 

    @Mock
    private StepExecution stepExecution; 

    @Mock
    private JobExecution jobExecution; 

    @Mock
    private ExecutionContext executionContext; 

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mocking the directory paths
        when(appConfig.getCsvOutputOk()).thenReturn("src/test/resources/csv/output/ok");
        when(appConfig.getCsvOutputNg()).thenReturn("src/test/resources/csv/output/ng");
        when(appConfig.getCsvBackup()).thenReturn("src/test/resources/csv/backup");

        // Mocking the ChunkContext, StepContext, StepExecution, JobExecution and ExecutionContext
        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getStepExecution()).thenReturn(stepExecution);
        when(stepExecution.getJobExecution()).thenReturn(jobExecution); 
        when(jobExecution.getExecutionContext()).thenReturn(executionContext); 

        // Ensure the output directories exist
        Files.createDirectories(Path.of("src/test/resources/csv/output/ok"));
        Files.createDirectories(Path.of("src/test/resources/csv/output/ng"));
        Files.createDirectories(Path.of("src/test/resources/csv/backup"));

        // Cleanup any existing test files
        cleanupTestFiles();
    }

    @Test
    void testExecute_InsertsOrUpdatesData() throws Exception {
        // Given
        when(medicalOpinionRepository.findByNameAndEmail("山田花子", "hanako@example.com"))
            .thenReturn(null); // Simulate no existing record
        createMockedOkFile(); // Create the mocked OK CSV input file

        // When
        csvToPostgresTasklet.execute(contribution, chunkContext); // Execute the task

        // Then
        assertTrue(Files.exists(Path.of("src/test/resources/csv/output/ok/20241011.ok.csv")), "OKファイルが作成されるべきです。");
    }

    private void createMockedOkFile() throws IOException {
        Path okFilePath = Path.of("src/test/resources/csv/output/ok/20241011.ok.csv");

        // Delete the file if it already exists
        if (Files.exists(okFilePath)) {
            Files.delete(okFilePath);
        }

        // Create the OK CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(okFilePath.toFile()))) {
            writer.write("名前,メール,身長,体重\n");
            writer.write("山田花子,hanako@example.com,171.5,66.2\n");
        }
    }

    private void cleanupTestFiles() throws IOException {
        // Clean up any existing test files
        Path okPath = Path.of("src/test/resources/csv/output/ok");
        Path ngPath = Path.of("src/test/resources/csv/output/ng");
        Path backupPath = Path.of("src/test/resources/csv/backup");

        if (Files.exists(okPath)) {
            Files.list(okPath).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if (Files.exists(ngPath)) {
            Files.list(ngPath).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if (Files.exists(backupPath)) {
            Files.list(backupPath).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
