package com.example.demo.config;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataSourceConfig {

//	private AppConfig appConfig;
//
//	public DataSourceConfig(AppConfig appConfig) {
//		this.appConfig = appConfig;
//	}

	// MySQL DataSource 설정
	@Primary
	@Bean(name = "mysqlDataSource")
	public DataSource mysqlDataSource() {
		return DataSourceBuilder.create()
				.url("jdbc:mysql://localhost:3307/batchdb")
				.username("root")
				.password("Root1234!")
				.driverClassName("com.mysql.cj.jdbc.Driver")
				.build();
	}

	// MySQL EntityManager 설정
	@Primary
	@Bean(name = "mysqlEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("mysqlDataSource") DataSource dataSource) {
		return builder
				.dataSource(dataSource)
				.packages("com.example.demo.model") // MySQL 관련 엔티티 패키지
				.persistenceUnit("mysqlPU")
				.build();
	}

	// MySQL TransactionManager 설정
	@Primary
	@Bean(name = "mysqlTransactionManager")
	public PlatformTransactionManager mysqlTransactionManager(
			@Qualifier("mysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	// PostgreSQL DataSource 설정
	@Bean(name = "postgresDataSource")
	public DataSource postgresDataSource() {
		return DataSourceBuilder.create()
				.url("jdbc:postgresql://localhost:5432/postgres")
				.username("postgres")
				.password("Root1234!")
				.driverClassName("org.postgresql.Driver")
				.build();
	}

	// PostgreSQL EntityManager 설정
	@Bean(name = "postgresEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("postgresDataSource") DataSource dataSource) {
		return builder
				.dataSource(dataSource)
				.packages("com.example.demo.model.postgres") // PostgreSQL 관련 엔티티 패키지
				.persistenceUnit("postgresPU")
				.build();
	}

	// PostgreSQL TransactionManager 설정
	@Bean(name = "postgresTransactionManager")
	public PlatformTransactionManager postgresTransactionManager(
			@Qualifier("postgresEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
