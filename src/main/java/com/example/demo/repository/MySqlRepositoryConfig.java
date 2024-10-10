package com.example.demo.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	    basePackages = "com.example.demo.repository.mysql",
	    entityManagerFactoryRef = "mysqlEntityManagerFactory",
	    transactionManagerRef = "mysqlTransactionManager"
	)
public class MySqlRepositoryConfig {
}
