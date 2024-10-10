package com.example.demo.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.mysql.UserInfoMySQL;
import com.example.demo.model.postgres.MedicalOpinionPostgres;
import com.example.demo.model.postgres.UserInfoPostgres;
import com.example.demo.repository.mysql.UserInfoMySQLRepository;
import com.example.demo.repository.postgres.MedicalOpinionPostgresRepository;
import com.example.demo.repository.postgres.UserInfoPostgresRepository;

@Service
public class DataService {
    private final UserInfoMySQLRepository userRepository;
    private final UserInfoPostgresRepository userInfoRepository;
    private final MedicalOpinionPostgresRepository medicalOpinionPostgresRepository;

    @Autowired
    public DataService(UserInfoMySQLRepository userRepository, UserInfoPostgresRepository userInfoRepository, MedicalOpinionPostgresRepository medicalOpinionPostgresRepository) {
        this.userRepository = userRepository;
        this.userInfoRepository = userInfoRepository;
        this.medicalOpinionPostgresRepository = medicalOpinionPostgresRepository;
    }
    
    public List<UserInfoMySQL> getMysqlUserInfoList(){
    	
    	return userRepository.findAll();
    }
    
    public List<UserInfoPostgres> getPostgresUserInfoList(){
    	
    	return userInfoRepository.findAll();
    }

	public List<MedicalOpinionPostgres> getPostgresMedicalOpinionList() {
		
		return medicalOpinionPostgresRepository.findAll();
	}
}
