package com.example.demo.tasklet;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.config.AppConfig;
import com.example.demo.model.UserInfo;
import com.example.demo.repository.UserInfoRepository;

import lombok.extern.slf4j.Slf4j;

@Component("HelloTasklet1")
@StepScope
@Slf4j
public class HelloTasklet1 implements Tasklet {

	@Value("#{jobParameters['param1']}")
	private String param1;
	@Value("#{jobParameters['param2']}")
	private String param2;
	private AppConfig appConfig; // AppConfig 주입
	private final UserInfoRepository userInfoRepository;

	// 생성자 주입 사용
	public HelloTasklet1(@Value("#{jobParameters['param1']}") String param1,
			@Value("#{jobParameters['param2']}") String param2,
			AppConfig appConfig,
			UserInfoRepository userInfoRepository) {
		this.param1 = param1;
		this.param2 = param2;
		this.appConfig = appConfig;
		this.userInfoRepository = userInfoRepository;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		log.info("Hello Tasklet1 !!!");
		log.info("param1={}", param1);
		log.info("param2={}", param2);
		//		System.out.println("Hello Tasklet1 !!!");
		//		System.out.println("param1="+param1);
		//		System.out.println("param2="+param2);
		// AppConfig의 chunkSize 값을 출력
		System.out.println("-----");
		System.out.println(appConfig.getDriver());
		System.out.println(appConfig.getPostgresDriverClassname());

		// userinfo 테이블의 모든 데이터를 가져오기
        List<UserInfo> userInfoList = userInfoRepository.findAll();

        // 가져온 데이터를 출력
        for (UserInfo userInfo : userInfoList) {
        	System.out.println("-----");
        	System.out.println(userInfo.getName() + " " + userInfo.getEmail());
        }
        

        
		ExecutionContext jobContext = contribution.getStepExecution()
				.getJobExecution()
				.getExecutionContext();
		jobContext.put("jobKey1", "jobValue1");

		return RepeatStatus.FINISHED;
	}

}
