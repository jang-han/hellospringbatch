package com.example.demo.tasklet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
import com.example.demo.model.mysql.UserInfoMySQL;
import com.example.demo.repository.DataService;

class CsvCompareAndWriteTaskletTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private DataService dataService;

    @InjectMocks
    private CsvCompareAndWriteTasklet csvCompareAndWriteTasklet;

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

        // Mocking the test directory paths
        when(appConfig.getCsvOutputOk()).thenReturn("src/test/resources/csv/output/ok");
        when(appConfig.getCsvOutputNg()).thenReturn("src/test/resources/csv/output/ng");
        when(appConfig.getCsvBackup()).thenReturn("src/test/resources/csv/backup");

        // Create the directories if they do not exist
        Files.createDirectories(Path.of("src/test/resources/csv/output/ok"));
        Files.createDirectories(Path.of("src/test/resources/csv/output/ng"));
        Files.createDirectories(Path.of("src/test/resources/csv/backup"));

        // Mocking the ChunkContext, StepContext, StepExecution, JobExecution and ExecutionContext
        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getStepExecution()).thenReturn(stepExecution);
        when(stepExecution.getJobExecution()).thenReturn(jobExecution);
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("movedFiles")).thenReturn(Arrays.asList("testFile.csv")); // Mocking the movedFiles
    }

    @Test
    void testExecute_CreatesOkNgFiles() throws Exception {
        // Given
        when(dataService.getMysqlUserInfoList()).thenReturn(Arrays.asList(new UserInfoMySQL("山田花子", "hanako@example.com")));
        createMockedCsvInput();

        // When
        csvCompareAndWriteTasklet.execute(contribution, chunkContext);

        // Then
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date()); // 오늘 날짜 구하기
        Path okFilePath = Path.of("src/test/resources/csv/output/ok/" + today + ".ok.csv"); // 날짜 포함
        Path ngFilePath = Path.of("src/test/resources/csv/output/ng/" + today + ".ng.csv"); // 날짜 포함

        // Log to check if files are created
        System.out.println("OK File exists: " + Files.exists(okFilePath));
        System.out.println("NG File exists: " + Files.exists(ngFilePath));

        // Verify the file creation
        assertTrue(Files.exists(okFilePath), "OK CSVファイルが作成されるべきです。");
        assertTrue(Files.exists(ngFilePath), "NG CSVファイルが作成されるべきです。");
    }

    private void createMockedCsvInput() throws IOException {
        // Create input directory
        Files.createDirectories(Path.of("src/test/resources/csv/input"));
        
        // Log the creation of the input directory
        System.out.println("Input directory created: " + "src/test/resources/csv/input");
        
        // Create CSV input file with initial data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/csv/input/testFile.csv"))) {
            writer.write("名前,メール,身長,体重\n");
            writer.write("山田花子,hanako@example.com,171.5,66.2\n");
            writer.write("佐藤健一,kenichi@example.com,,55.3\n"); // Missing height
        }
        
        // Log the creation of the CSV file
        System.out.println("CSV input file created: " + "src/test/resources/csv/input/testFile.csv");
    }
}
