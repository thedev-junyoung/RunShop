# **RunShop - 온라인 쇼핑몰 시스템**
## **목차**
1. [프로젝트 개요](#프로젝트-개요)
2. [주요 기능](#주요-기능)
3. [시스템 요구 사항](#시스템-요구-사항)
4. [설치 및 실행 가이드](#설치-및-실행-가이드)
   - [1. 저장소 클론](#1-저장소-클론)
   - [2. 테스트 프로파일로 빌드 및 테스트](#2-테스트-프로파일로-빌드-및-테스트)
   - [3. Docker 컨테이너 실행](#3-docker-컨테이너-실행)

---

## **프로젝트 개요**
RunShop은 사용자가 상품을 조회하고, 장바구니에 담아 주문 및 결제를 진행할 수 있는 기능을 제공하는 온라인 쇼핑몰 시스템입니다.  
사용자는 판매자로 등록하여 상품을 업로드하고 관리할 수 있습니다.  
또한, 이 프로젝트는 **DDD(Domain-Driven Design)**와 **Hexagonal Architecture**를 도입하여 결제와 주문 시스템을 구축했습니다.

---

## **설치 및 실행 가이드**

### **1. 저장소 클론**
```bash
git clone https://github.com/thedev-junyoung/RunShop.git 
cd RunShop
```

### **2. 테스트 프로파일로 빌드 및 테스트**
```bash
./gradlew clean build -Dspring.profiles.active=test
```

### **3. Docker 컨테이너 실행**
```bash
docker-compose up -d
```
컨테이너 실행 확인
```bash
docker-compose ps
```
runshop-app, mysql, redis 컨테이너가 모두 Running 상태인지 확인.



