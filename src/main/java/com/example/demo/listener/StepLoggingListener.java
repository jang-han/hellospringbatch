package com.example.demo.listener;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class StepLoggingListener implements StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(StepLoggingListener.class);
    private Instant start;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        start = Instant.now();
        logger.info("タスクを開始しました - タスク名: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        Instant end = Instant.now();
        long duration = Duration.between(start, end).getSeconds();
        logger.info("タスクが終了しました - タスク名: {}, 所要時間: {}秒", stepExecution.getStepName(), duration);
        return ExitStatus.COMPLETED;
    }
}

