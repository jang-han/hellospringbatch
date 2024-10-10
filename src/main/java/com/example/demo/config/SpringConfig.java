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

@Configuration
public class SpringConfig {

	private final JobLauncher jobLauncher;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final AppConfig appConfig;
	
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
			PlatformTransactionManager transactionManager, AppConfig appConfig) {
		this.jobLauncher = jobLauncher;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.appConfig = appConfig;
	}
	
	@Bean
	public Step deleteOldFilesTaskletStep() {
		return new StepBuilder("deleteOldFilesTaskletStep", jobRepository)
				.tasklet(deleteOldFilesTasklet, transactionManager)
				.build();
	}
	
	@Bean
	public Step csvFileCheckAndMoveTaskletStep() {
		return new StepBuilder("csvFileCheckAndMoveTaskletStep", jobRepository)
				.tasklet(csvFileCheckAndMoveTasklet, transactionManager)
				.build();
	}
	
	@Bean
	public Step csvCompareAndWriteTaskletStep() {
		return new StepBuilder("csvCompareAndWriteTaskletStep", jobRepository)
				.tasklet(csvCompareAndWriteTasklet, transactionManager)
				.build();
	}
	
	@Bean
	public Step csvToPostgresTaskletStep() {
		return new StepBuilder("csvToPostgresTaskletStep", jobRepository)
				.tasklet(csvToPostgresTasklet, transactionManager)
				.build();
	}
	
	@Bean
	public Job helloJob() {
		System.out.println("-----");
		System.out.println(appConfig.getApplicationName());
		return new JobBuilder("helloJob", jobRepository)
				.incrementer(new RunIdIncrementer())
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
