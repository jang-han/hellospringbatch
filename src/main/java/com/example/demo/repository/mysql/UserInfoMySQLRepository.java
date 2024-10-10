package com.example.demo.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.mysql.UserInfoMySQL;

public interface UserInfoMySQLRepository extends JpaRepository<UserInfoMySQL, Long> {
}
