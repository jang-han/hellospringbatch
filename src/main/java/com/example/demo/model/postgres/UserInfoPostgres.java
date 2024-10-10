package com.example.demo.model.postgres;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity(name = "UserInfoPostgres") // 다른 엔티티 이름 지정
@Table(name = "userinfo")
@Data
public class UserInfoPostgres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 자동 생성 설정
    private Long id;
    private String name;
    private String email;

    // Getters and setters
}
