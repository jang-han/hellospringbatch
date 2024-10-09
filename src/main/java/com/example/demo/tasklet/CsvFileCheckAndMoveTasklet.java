package com.example.demo.tasklet;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.example.demo.config.AppConfig;

@Component("CsvFileCheckAndMoveTasklet")
@StepScope
public class CsvFileCheckAndMoveTasklet implements Tasklet {

    private final AppConfig appConfig;

    public CsvFileCheckAndMoveTasklet(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        File inputDir = new File(appConfig.getCsvInput());
        File backupDir = new File(appConfig.getCsvBackup());

        // CSV 파일 이름을 저장할 리스트
        List<String> movedFiles = new ArrayList<>();

        // Input 폴더가 존재하고 디렉토리인지 확인
        if (inputDir.exists() && inputDir.isDirectory()) {
            File[] csvFiles = inputDir.listFiles((dir, name) -> name.endsWith(".csv"));

            if (csvFiles != null && csvFiles.length > 0) {
                // Backup 폴더가 없으면 생성
                if (!backupDir.exists()) {
                    backupDir.mkdirs();
                }

                // 파일 이동 및 리스트에 추가
                for (File csvFile : csvFiles) {
                    Path sourcePath = csvFile.toPath();
                    Path destinationPath = new File(backupDir, csvFile.getName()).toPath();
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    movedFiles.add(csvFile.getName());
                }
                System.out.println("CSV 파일이 백업 폴더로 이동되었습니다.");
            } else {
                System.out.println("CSV 파일이 없습니다.");
            }
        } else {
            System.out.println("Input 폴더가 존재하지 않거나 디렉토리가 아닙니다.");
        }

        // 파일 이름 리스트를 ExecutionContext에 저장하여 다음 태스크에 전달
        ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        jobContext.put("movedFiles", movedFiles);
        return RepeatStatus.FINISHED;
    }
}
