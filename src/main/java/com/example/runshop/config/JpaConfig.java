package com.example.runshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.runshop.repository")  // JPA 리포지토리 패키지 지정
public class JpaConfig {
    // 필요한 경우 추가 JPA 설정을 작성
}
