package com.example.demo.listener;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobLoggingListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobLoggingListener.class);
    private Instant start;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        start = Instant.now();
        logger.info("ジョブを開始しました - ジョブID: {}", jobExecution.getJobId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Instant end = Instant.now();
        long duration = Duration.between(start, end).getSeconds();
        logger.info("ジョブが終了しました - ジョブID: {}, 所要時間: {}秒", jobExecution.getJobId(), duration);
    }
}

