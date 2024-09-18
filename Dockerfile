# 베이스 이미지를 사용하여 자바와 함께 애플리케이션을 실행
FROM openjdk:17-jdk-slim

# Gradle 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/shoppingmall-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행 명령 (프로파일을 dev로 실행하도록 설정)
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=docker"]
