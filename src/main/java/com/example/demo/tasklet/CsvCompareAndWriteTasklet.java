package com.example.demo.tasklet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.config.AppConfig;
import com.example.demo.model.UserInfo;
import com.example.demo.service.UserInfoService;

@Component("CsvCompareAndWriteTasklet")
@StepScope
public class CsvCompareAndWriteTasklet implements Tasklet {

    private final AppConfig appConfig;
    private final UserInfoService userInfoService;

    @Autowired
    public CsvCompareAndWriteTasklet(UserInfoService userInfoService, AppConfig appConfig) {
        this.userInfoService = userInfoService;
        this.appConfig = appConfig;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<String> csvFileNames = (List<String>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("movedFiles");
        List<UserInfo> userInfoList = userInfoService.fetchAllUserInfo();

        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

        for (String fileName : csvFileNames) {
            File csvFile = new File(appConfig.getCsvBackup(), fileName);
            if (!csvFile.exists()) continue;

            String okFilePath = appConfig.getCsvOutputOk() + "/" + today + ".ok.csv";
            String ngFilePath = appConfig.getCsvOutputNg() + "/" + today + ".ng.csv";
            String okEndFilePath = appConfig.getCsvOutputOk() + "/" + today + ".ok.end";
            String ngEndFilePath = appConfig.getCsvOutputNg() + "/" + today + ".ng.end";

            try (
                BufferedReader reader = new BufferedReader(new FileReader(csvFile));
                BufferedWriter okWriter = new BufferedWriter(new FileWriter(okFilePath, true));
                BufferedWriter ngWriter = new BufferedWriter(new FileWriter(ngFilePath, true))
            ) {
                processFile(reader, okWriter, ngWriter, userInfoList);

                createEndFile(okEndFilePath);
                createEndFile(ngEndFilePath);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error processing file: " + csvFile.getName(), e);
            }
        }
        return RepeatStatus.FINISHED;
    }

    private void processFile(BufferedReader reader, BufferedWriter okWriter, BufferedWriter ngWriter, List<UserInfo> userInfoList) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] csvData = line.split(",");
            if (csvData.length < 2) {
                System.out.println("Invalid line in CSV: " + line);
                continue;
            }

            String name = csvData[0].trim();
            String email = csvData[1].trim();

            boolean matched = userInfoList.stream()
                .anyMatch(user -> user.getName().equals(name) && user.getEmail().equals(email));

            if (matched) {
                writeLine(okWriter, name, email);
            } else {
                writeLine(ngWriter, name, email);
            }
        }
    }

    private void writeLine(BufferedWriter writer, String name, String email) throws IOException {
        writer.write(name + "," + email);
        writer.newLine();
    }

    private void createEndFile(String filePath) {
        try {
            Files.createFile(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating end file at: " + filePath);
        }
    }
}
