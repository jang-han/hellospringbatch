package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.config.AppConfig;

@Service
public class BatchJobService {

	private final AppConfig appConfig;
	
	@Autowired
	public BatchJobService(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
}
