# 베이스 이미지를 사용하여 자바와 함께 애플리케이션을 실행
FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일을 컨테이너에 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션이 사용하는 포트 노출 (기본 8080)
EXPOSE 8080

# 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=docker"]
