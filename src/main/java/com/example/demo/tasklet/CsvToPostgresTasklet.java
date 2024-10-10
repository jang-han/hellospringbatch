package com.example.demo.tasklet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.example.demo.config.AppConfig;
import com.example.demo.model.postgres.MedicalOpinionPostgres;
import com.example.demo.repository.postgres.MedicalOpinionPostgresRepository;

@Component("CsvToPostgresTasklet")
@StepScope
public class CsvToPostgresTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(CsvToPostgresTasklet.class);
    private final AppConfig appConfig;
    private final MedicalOpinionPostgresRepository medicalOpinionRepository;

    @Autowired
    public CsvToPostgresTasklet(AppConfig appConfig, @Qualifier("postgresMedicalOpinionRepository") MedicalOpinionPostgresRepository medicalOpinionRepository) {
        this.appConfig = appConfig;
        this.medicalOpinionRepository = medicalOpinionRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("CsvToPostgresTasklet 処理を開始します。");

        String okFilePath = getOkFilePath();
        File okFile = new File(okFilePath);

        if (!okFile.exists()) {
            logger.warn("OK CSV ファイルが存在しません: {}", okFilePath);
            return RepeatStatus.FINISHED;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(okFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                processLine(line);
            }
            logger.info("PostgreSQLへのデータ登録と更新が完了しました。");
        } catch (IOException e) {
            logger.error("ファイルの読み込みエラー: {}", okFile.getName(), e);
            throw new RuntimeException("ファイルの読み込みエラー: " + okFile.getName(), e);
        }

        return RepeatStatus.FINISHED;
    }

    private String getOkFilePath() {
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return Paths.get(appConfig.getCsvOutputOk(), today + ".ok.csv").toString();
    }

    private void processLine(String line) {
        String[] csvData = line.split(",");
        if (csvData.length < 4) {
            logger.warn("CSV データの形式が不正です: {}", line);
            return;
        }

        String name = csvData[0].trim();
        String email = csvData[1].trim();
        float height;
        float weight;

        try {
            height = Float.parseFloat(csvData[2].trim());
            weight = Float.parseFloat(csvData[3].trim());
        } catch (NumberFormatException e) {
            logger.error("身長または体重の形式が不正です: {}", line, e);
            return;
        }

        MedicalOpinionPostgres existingRecord = medicalOpinionRepository.findByNameAndEmail(name, email);
        if (existingRecord != null) {
            existingRecord.setHeight(height);
            existingRecord.setWeight(weight);
            medicalOpinionRepository.save(existingRecord);
            logger.info("既存のレコードを更新しました: {}", name);
        } else {
            MedicalOpinionPostgres newRecord = new MedicalOpinionPostgres();
            newRecord.setName(name);
            newRecord.setEmail(email);
            newRecord.setHeight(height);
            newRecord.setWeight(weight);
            medicalOpinionRepository.save(newRecord);
            logger.info("新しいレコードを追加しました: {}", name);
        }
    }
}
