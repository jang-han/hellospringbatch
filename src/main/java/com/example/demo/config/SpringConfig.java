package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.listener.JobLoggingListener;
import com.example.demo.listener.StepLoggingListener;

@Configuration
public class SpringConfig {

	private final JobLauncher jobLauncher;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final AppConfig appConfig;
	private final JobLoggingListener jobLoggingListener;
    private final StepLoggingListener stepLoggingListener;
	
	@Autowired
	@Qualifier("DeleteOldFilesTasklet")
	private Tasklet deleteOldFilesTasklet;
	
	@Autowired
	@Qualifier("CsvFileCheckAndMoveTasklet")
	private Tasklet csvFileCheckAndMoveTasklet;
	
	@Autowired
	@Qualifier("csvCompareAndWriteTasklet")
	private Tasklet csvCompareAndWriteTasklet;
	
	@Autowired
	@Qualifier("CsvToPostgresTasklet")
	private Tasklet csvToPostgresTasklet;
	
	public SpringConfig(JobLauncher jobLauncher, JobRepository jobRepository,
			PlatformTransactionManager transactionManager, AppConfig appConfig,
			JobLoggingListener jobLoggingListener, StepLoggingListener stepLoggingListener) {
		this.jobLauncher = jobLauncher;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.appConfig = appConfig;
		this.jobLoggingListener = jobLoggingListener;
        this.stepLoggingListener = stepLoggingListener;
	}
	
	@Bean
	public Step deleteOldFilesTaskletStep() {
		return new StepBuilder("deleteOldFilesTaskletStep", jobRepository)
				.tasklet(deleteOldFilesTasklet, transactionManager)
				.listener(stepLoggingListener)
				.build();
	}
	
	@Bean
	public Step csvFileCheckAndMoveTaskletStep() {
		return new StepBuilder("csvFileCheckAndMoveTaskletStep", jobRepository)
				.tasklet(csvFileCheckAndMoveTasklet, transactionManager)
				.listener(stepLoggingListener)
				.build();
	}
	
	@Bean
	public Step csvCompareAndWriteTaskletStep() {
		return new StepBuilder("csvCompareAndWriteTaskletStep", jobRepository)
				.tasklet(csvCompareAndWriteTasklet, transactionManager)
				.listener(stepLoggingListener)
				.build();
	}
	
	@Bean
	public Step csvToPostgresTaskletStep() {
		return new StepBuilder("csvToPostgresTaskletStep", jobRepository)
				.tasklet(csvToPostgresTasklet, transactionManager)
				.listener(stepLoggingListener)
				.build();
	}
	
	@Bean
	public Job helloJob() {
		System.out.println("-----");
		System.out.println(appConfig.getApplicationName());
		return new JobBuilder("helloJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(jobLoggingListener)
				.start(deleteOldFilesTaskletStep())
				.next(csvFileCheckAndMoveTaskletStep())
				.next(csvCompareAndWriteTaskletStep())
				.next(csvToPostgresTaskletStep())
//				.next(helloChunkStep())
//				.validator(jobParametersValidator())
//				.listener(helloJobExecutionListner)
				.build();
	}
}
