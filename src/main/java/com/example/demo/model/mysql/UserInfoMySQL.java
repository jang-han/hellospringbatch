package com.example.demo.model.mysql;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity(name = "UserInfoMySQL") // 엔티티 이름 지정
@Table(name = "UserInfo")
@Data
public class UserInfoMySQL {
    @Id
    private Long id;
    private String name;
    private String email;

    // Getters and setters
}
