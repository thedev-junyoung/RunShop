package com.example.shoppingmall.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.shoppingmall.filter.JWTFilter;
import com.example.shoppingmall.utils.JWT;

//@Configuration
public class FilterConfig {
    private final JWT jwt;

    public FilterConfig(JWT jwt) {
        this.jwt = jwt;
    }

    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilter() {
        FilterRegistrationBean<JWTFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JWTFilter(jwt));
        registrationBean.addUrlPatterns("/api/*"); // 필터를 적용할 URL 패턴 지정

        return registrationBean;
    }
}
