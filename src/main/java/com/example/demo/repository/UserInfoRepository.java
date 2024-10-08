package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    // 필요한 경우 사용자 정의 메서드를 추가할 수 있습니다.
}
