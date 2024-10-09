package com.example.demo.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.config.AppConfig;
import com.example.demo.model.UserInfo;

@Service
public class UserInfoService {

	private AppConfig appConfig;
	
	public UserInfoService (AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	
    public List<UserInfo> fetchAllUserInfo() {
        String url = appConfig.getPostgresUrl();
        String username = appConfig.getPostgresUsername();
        String password = appConfig.getPostgresPassword();
        List<UserInfo> userInfoList = new ArrayList<>();

        // PostgreSQL에 직접 연결
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to PostgreSQL database!");

            String query = "SELECT name, email FROM userinfo";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setName(resultSet.getString("name"));
                    userInfo.setEmail(resultSet.getString("email"));
                    userInfoList.add(userInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userInfoList;
    }
}
