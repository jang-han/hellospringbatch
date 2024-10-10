package com.example.demo.tasklet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.config.AppConfig;
import com.example.demo.model.mysql.UserInfoMySQL;
import com.example.demo.repository.DataService;

@Component("csvCompareAndWriteTasklet")
@StepScope
public class CsvCompareAndWriteTasklet implements Tasklet {

    private final AppConfig appConfig;
    private final DataService dataService;

    @Autowired
    public CsvCompareAndWriteTasklet(DataService dataService, AppConfig appConfig) {
        this.dataService = dataService;
        this.appConfig = appConfig;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<String> csvFileNames = (List<String>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("movedFiles");
        List<UserInfoMySQL> userInfoList = dataService.getMysqlUserInfoList();

        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String okFilePath = appConfig.getCsvOutputOk() + "/" + today + ".ok.csv";
        String ngFilePath = appConfig.getCsvOutputNg() + "/" + today + ".ng.csv";
        String okEndFilePath = appConfig.getCsvOutputOk() + "/" + today + ".ok.end";
        String ngEndFilePath = appConfig.getCsvOutputNg() + "/" + today + ".ng.end";

        try (
            BufferedWriter okWriter = new BufferedWriter(new FileWriter(okFilePath));
            BufferedWriter ngWriter = new BufferedWriter(new FileWriter(ngFilePath))
        ) {
            // 헤더 추가 (일본어)
            okWriter.write("名前,メール,身長,体重");
            okWriter.newLine();
            ngWriter.write("名前,メール,身長,体重");
            ngWriter.newLine();

            for (String fileName : csvFileNames) {
                File csvFile = new File(appConfig.getCsvBackup(), fileName);
                if (!csvFile.exists()) continue;

                try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                    processFile(reader, okWriter, ngWriter, userInfoList);
                }
            }

            createOrOverwriteEndFile(okEndFilePath);
            createOrOverwriteEndFile(ngEndFilePath);

            ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            jobContext.put("okFilePath", okFilePath);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing files", e);
        }
        return RepeatStatus.FINISHED;
    }

    private void processFile(BufferedReader reader, BufferedWriter okWriter, BufferedWriter ngWriter, List<UserInfoMySQL> userInfoList) throws IOException {
    	boolean isFirstLine = true;
    	String line;
        while ((line = reader.readLine()) != null) {
        	if (isFirstLine) {
                isFirstLine = false;
                continue; // 첫 줄은 건너뜁니다.
            }
        	
            String[] csvData = line.split(",");
            if (csvData.length < 4) {
                System.out.println("Invalid line in CSV: " + line);
                writeLine(ngWriter, csvData);
                continue;
            }

            String name = csvData[0].trim();
            String email = csvData[1].trim();
            String height = csvData[2].trim();
            String weight = csvData[3].trim();

            // 키 또는 몸무게가 없는 경우 ng 파일에 기록
            if (height.isEmpty() || weight.isEmpty()) {
                writeLine(ngWriter, name, email, height, weight);
                continue;
            }

            boolean matchedInUserInfo = userInfoList.stream()
                    .anyMatch(user -> user.getName().equals(name) && user.getEmail().equals(email));

            if (matchedInUserInfo) {
                writeLine(okWriter, name, email, height, weight);
            } else {
                writeLine(ngWriter, name, email, height, weight);
            }
        }
    }

    private void writeLine(BufferedWriter writer, String name, String email, String height, String weight) throws IOException {
        writer.write(name + "," + email + "," + height + "," + weight);
        writer.newLine();
    }

    private void writeLine(BufferedWriter writer, String[] csvData) throws IOException {
        writer.write(String.join(",", csvData));
        writer.newLine();
    }

    private void createOrOverwriteEndFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating end file at: " + filePath);
        }
    }
}
