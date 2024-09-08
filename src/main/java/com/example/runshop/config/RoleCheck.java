package com.example.runshop.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 메서드에만 사용 가능하게 설정
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleCheck { // 역할을 체크하는 애노테이션
    String[] value() default {};  // 필요한 역할을 배열로 설정 가능
}
