package com.example.demo.tasklet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private final AppConfig appConfig;
    private final MedicalOpinionPostgresRepository medicalOpinionRepository;

    @Autowired
    public CsvToPostgresTasklet(AppConfig appConfig, @Qualifier("postgresMedicalOpinionRepository") MedicalOpinionPostgresRepository medicalOpinionRepository) {
        this.appConfig = appConfig;
        this.medicalOpinionRepository = medicalOpinionRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("-- CsvToPostgresTasklet start -- ");

        String okFilePath = getOkFilePath();
        File okFile = new File(okFilePath);

        if (!okFile.exists()) {
            System.out.println("OK CSV 파일이 존재하지 않습니다.");
            return RepeatStatus.FINISHED;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(okFile))) {
            String line;
            boolean isFirstLine = true; // 첫 번째 줄 스킵용

            while ((line = reader.readLine()) != null) {
                // 첫 번째 줄은 헤더이므로 건너뜀
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                processLine(line);
            }
            System.out.println("PostgreSQL에 데이터 삽입 및 갱신 완료");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 읽기 오류: " + okFile.getName(), e);
        }

        return RepeatStatus.FINISHED;
    }

    // OK 파일 경로를 오늘 날짜 기준으로 설정
    private String getOkFilePath() {
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return Paths.get(appConfig.getCsvOutputOk(), today + ".ok.csv").toString();
    }

    // 라인별 데이터 처리
    private void processLine(String line) {
        String[] csvData = line.split(",");
        if (csvData.length < 4) {  // 네 개의 컬럼이 있어야 함
            System.out.println("잘못된 데이터 형식: " + line);
            return;
        }

        String name = csvData[0].trim();
        String email = csvData[1].trim();
        float height = Float.parseFloat(csvData[2].trim());
        float weight = Float.parseFloat(csvData[3].trim());

        // 데이터 존재 여부 확인 및 삽입/갱신
        MedicalOpinionPostgres existingRecord = medicalOpinionRepository.findByNameAndEmail(name, email);
        if (existingRecord != null) {
            // 같은 이름과 이메일이 존재할 경우 키와 몸무게를 갱신
            existingRecord.setHeight(height);
            existingRecord.setWeight(weight);
            medicalOpinionRepository.save(existingRecord);
            System.out.println("기존 의견서 갱신: " + name);
        } else {
            // 새 레코드를 추가
            MedicalOpinionPostgres newRecord = new MedicalOpinionPostgres();
            newRecord.setName(name);
            newRecord.setEmail(email);
            newRecord.setHeight(height);
            newRecord.setWeight(weight);
            medicalOpinionRepository.save(newRecord);
            System.out.println("새 의견서 추가: " + name);
        }
    }
}
