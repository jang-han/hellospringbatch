package com.example.demo.model.mysql;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "UserInfoMySQL") // 엔티티 이름 지정
@Table(name = "UserInfo")
@Data
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 포함한 생성자
public class UserInfoMySQL {
    @Id
    private Long id;
    private String name;
    private String email;

    // 매개변수가 2개인 생성자 추가
    public UserInfoMySQL(String name, String email) {
        this.name = name;
        this.email = email;
    }
    // Getters and setters
}
