package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Value("${spring.application.name}")
	private String applicationName;
	
	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.driver-class-name}")
	private String driver;

	@Value("${app.datasource.postgres.url}")
	private String postgresUrl;
	
	@Value("${app.datasource.postgres.username}")
	private String postgresUsername;
	
	@Value("${app.datasource.postgres.password}")
	private String postgresPassword;
	
	@Value("${app.datasource.postgres.driver-class-name}")
	private String postgresDriverClassname;
	
	
	@Value("${app.csv.input}")
	private String csvInput;
	
	@Value("${app.csv.output.ok}")
	private String csvOutputOk;
	
	@Value("${app.csv.output.ng}")
	private String csvOutputNg;
	
	@Value("${app.csv.backup}")
	private String csvBackup;
	
	@Value("${batch.process.chunk.size}")
    private int chunkSize;
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getDriver() {
		return driver;
	}
	
	public String getPostgresUrl() {
		return postgresUrl;
	}
	
	public String getPostgresUsername() {
		return postgresUsername;
	}
	
	public String getPostgresPassword() {
		return postgresPassword;
	}
	
	public String getPostgresDriverClassname() {
		return postgresDriverClassname;
	}
	
	public String getCsvInput() {
		return csvInput;
	}
	
	public String getCsvOutputOk() {
		return csvOutputOk;
	}
	
	public String getCsvOutputNg() {
		return csvOutputNg;
	}
	
	public String getCsvBackup() {
		return csvBackup;
	}
	
	public int getChunkSize() {
        return chunkSize;
    }
}
