package com.example.demo.chunk;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@StepScope
@Slf4j
public class HelloReader implements ItemReader<String>{

	private int intIndex = 0;
	
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
	
		String[] readDataList = {"associate", "consultant", "manager", "director", "president", null};
		
		log.info("Read Data={}", readDataList[intIndex]);
	
		return readDataList[intIndex++];
	}

}
