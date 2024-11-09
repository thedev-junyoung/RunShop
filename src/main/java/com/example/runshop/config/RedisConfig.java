package com.example.runshop.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * RedisConfig 클래스는 Spring Cache와 Redis를 사용하여 애플리케이션의 캐싱을 구성합니다.
 * 이 클래스는 Redis 캐시에 저장되는 데이터의 직렬화/역직렬화를 설정하고,
 * 캐시의 유효 시간과 Null 값 캐싱 비활성화 등을 설정합니다.
 */
@Configuration // Spring의 설정 파일임을 나타내며, Bean 정의와 설정을 포함합니다.
@EnableCaching // Spring의 캐싱 기능을 활성화하여 캐시를 사용 가능하게 합니다.
public class RedisConfig {

    /**
     * Redis 캐시 구성을 정의하는 메소드입니다.
     * RedisCacheConfiguration Bean을 생성하여 캐시에 대한 직렬화 설정 및 TTL(캐시 유효 시간)을 정의합니다.
     *
     * @return RedisCacheConfiguration 객체를 반환하여 캐시 설정을 적용합니다.
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // ObjectMapper는 직렬화 및 역직렬화 시 사용되는 JSON Mapper입니다.
        ObjectMapper objectMapper = new ObjectMapper();

        // JavaTimeModule을 등록하여 LocalDateTime 등의 Java 8 시간 API를 직렬화할 수 있도록 지원합니다.
        objectMapper.registerModule(new JavaTimeModule());

        // 다형성 객체의 직렬화 시 타입 정보를 포함하도록 설정합니다.
        // PolymorphicTypeValidator는 안전한 타입 검증을 위한 설정을 정의합니다.
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class) // 모든 Object 타입을 기반으로 직렬화가 가능하도록 설정합니다.
                .build();

        // 다형성 타입 처리를 위한 기본 타이핑을 활성화하여,
        // 직렬화할 때 WRAPPER_ARRAY를 사용해 타입 정보를 포함하도록 설정합니다.
        objectMapper.activateDefaultTyping(
                ptv, // 안전한 타입 검증 설정을 적용합니다.
                ObjectMapper.DefaultTyping.NON_FINAL, // 비최종(final) 타입에 대해 다형성 타입 처리를 활성화합니다.
                JsonTypeInfo.As.WRAPPER_ARRAY // 타입 정보를 배열 형태로 감싸 직렬화합니다.
        );

        // GenericJackson2JsonRedisSerializer는 Redis에 저장되는 객체를 JSON으로 직렬화하는 데 사용됩니다.
        // 이 직렬화기를 사용하면 Redis에 저장되는 모든 객체가 JSON 형식으로 변환됩니다.
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // RedisCacheConfiguration 객체를 생성하여 캐시 설정을 구성합니다.
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60)) // 캐시 항목의 유효 시간을 60분으로 설정합니다.
                .disableCachingNullValues() // Null 값을 캐싱하지 않도록 설정하여, 불필요한 캐시 저장을 방지합니다.
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // 캐시 키를 String으로 직렬화합니다.
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)); // 캐시 값을 JSON 형식으로 직렬화합니다.
    }
}
