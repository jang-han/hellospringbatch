package com.example.demo.model.postgres;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_opinion")
@Data
@NoArgsConstructor
public class MedicalOpinionPostgres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 기본 키

    private String name;    // 이름
    private String email;   // 이메일
    private double height;  // 키 (cm)
    private double weight;  // 몸무게 (kg)

    public MedicalOpinionPostgres(String name, String email, double height, double weight) {
        this.name = name;
        this.email = email;
        this.height = height;
        this.weight = weight;
    }
}
