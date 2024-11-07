package com.example.runshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"com.example.runshop.module.order.adapters.out.persistence",
		"com.example.runshop.module.payment.adapters.out.persistence" // PaymentJpaRepository 패키지 추가
})
public class RunShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(RunShopApplication.class, args);
	}

}
