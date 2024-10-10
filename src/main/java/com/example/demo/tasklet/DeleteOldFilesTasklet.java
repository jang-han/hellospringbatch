package com.example.demo.tasklet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.config.AppConfig;

@Component("DeleteOldFilesTasklet")
@StepScope
public class DeleteOldFilesTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(DeleteOldFilesTasklet.class);
    
    private final AppConfig appConfig;

    @Autowired
    public DeleteOldFilesTasklet(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Instant oneYearAgo = Instant.now().minus(365, ChronoUnit.DAYS);

        List<String> directories = Arrays.asList(
                appConfig.getCsvOutputOk(),
                appConfig.getCsvOutputNg(),
                appConfig.getCsvBackup()
        );

        for (String dirPath : directories) {
            File directory = new File(dirPath);

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles((dir, name) -> name.endsWith(".csv") || name.endsWith(".end"));

                if (files != null) {
                    for (File file : files) {
                        try {
                            Path filePath = file.toPath();
                            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
                            Instant fileLastModified = attr.lastModifiedTime().toInstant();

                            if (fileLastModified.isBefore(oneYearAgo)) {
                                boolean deleted = file.delete();
                                if (deleted) {
                                    logger.info("古いファイルを削除しました: {}", file.getAbsolutePath());
                                } else {
                                    logger.warn("ファイルの削除に失敗しました（権限の問題かファイル使用中）: {}", file.getAbsolutePath());
                                }
                            }
                        } catch (IOException e) {
                            logger.error("ファイル属性の読み取りエラー: {}", file.getAbsolutePath(), e);
                        }
                    }
                }
            } else {
                logger.warn("ディレクトリが存在しないかアクセスできません: {}", dirPath);
            }
        }

        return RepeatStatus.FINISHED;
    }
}
