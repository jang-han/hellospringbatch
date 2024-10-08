package com.example.demo.tasklet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        

        
        
        
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "Root1234!";
        
        List<UserInfo> userInfoList1 = new ArrayList<>();

        // PostgreSQL에 직접 연결
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to PostgreSQL database!");

            // 쿼리를 실행하고 결과를 처리
            String query = "SELECT name, email FROM userinfo";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setName(resultSet.getString("name"));
                    userInfo.setEmail(resultSet.getString("email"));
                    userInfoList1.add(userInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 가져온 데이터 출력
        userInfoList1.forEach(userInfo -> System.out.println("User Info: " + userInfo));
        
        
        
        
        
        
        
        
        
        
		ExecutionContext jobContext = contribution.getStepExecution()
				.getJobExecution()
				.getExecutionContext();
		jobContext.put("jobKey1", "jobValue1");

		return RepeatStatus.FINISHED;
	}

}
