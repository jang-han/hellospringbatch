package com.example.demo.tasklet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CsvFileCheckAndMoveTasklet.class);
    
    private final AppConfig appConfig;

    public CsvFileCheckAndMoveTasklet(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        File inputDir = new File(appConfig.getCsvInput());
        File backupDir = new File(appConfig.getCsvBackup());

        List<String> movedFiles = new ArrayList<>();

        // Input 폴더 확인
        if (inputDir.exists() && inputDir.isDirectory()) {
            File[] csvFiles = inputDir.listFiles((dir, name) -> name.endsWith(".csv"));

            if (csvFiles != null && csvFiles.length > 0) {
                if (!backupDir.exists() && !backupDir.mkdirs()) {
                    logger.error("バックアップフォルダを作成できませんでした: {}", backupDir.getAbsolutePath());
                    throw new IOException("バックアップフォルダの作成に失敗しました");
                }

                // 파일 이동 및 리스트에 추가
                for (File csvFile : csvFiles) {
                    Path sourcePath = csvFile.toPath();
                    Path destinationPath = new File(backupDir, csvFile.getName()).toPath();
                    try {
                        Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        movedFiles.add(csvFile.getName());
                        logger.info("CSVファイルがバックアップフォルダに移動されました: {}", csvFile.getName());
                    } catch (IOException e) {
                        logger.error("ファイル移動中にエラーが発生しました: {}", csvFile.getName(), e);
                    }
                }
            } else {
                logger.warn("CSVファイルが見つかりません。");
            }
        } else {
            logger.warn("入力フォルダが存在しないか、ディレクトリではありません: {}", inputDir.getAbsolutePath());
        }

        // 파일 이름 리스트를 ExecutionContext에 저장하여 다음 태스크에 전달
        ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        jobContext.put("movedFiles", movedFiles);
        return RepeatStatus.FINISHED;
    }
}
