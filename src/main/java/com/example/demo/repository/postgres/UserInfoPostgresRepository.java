package com.example.demo.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.postgres.UserInfoPostgres;

@Repository("postgresUserInfoRepository")
public interface UserInfoPostgresRepository extends JpaRepository<UserInfoPostgres, Long> {
    UserInfoPostgres findByName(String name); // name으로 조회
}
