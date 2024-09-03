package com.example.runshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.runshop.model.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    User findByEmail(String email);
    Optional<User> findByNameAndPassword(String username, String password);
    Optional<User> findByEmailAndPassword(String email, String password);
    Boolean existsByEmail(String email);
    // JPA 메서드 네이밍 전략에 따른 메서드 정의
    // findBy: 특정 조건을 기준으로 데이터를 조회
    // existsBy: 특정 조건에 따라 데이터의 존재 여부를 확인
    // And: 두 가지 조건을 모두 만족하는 데이터를 조회
    // Or: 두 가지 조건 중 하나 이상을 만족하는 데이터를 조회
    // In: 특정 조건에 해당하는 데이터를 조회
    // NotIn: 특정 조건에 해당하지 않는 데이터를 조회
    // Between: 특정 범위에 해당하는 데이터를 조회
    // LessThan: 특정 값보다 작은 데이터를 조회
    // GreaterThan: 특정 값보다 큰 데이터를 조회
    // Like: 특정 문자열을 포함하는 데이터를 조회
    // OrderBy: 특정 컬럼을 기준으로 데이터를 정렬
    // Asc: 오름차순 정렬
    // Desc: 내림차순 정렬
    // Pageable: 페이징 처리를 위한 메서드
    // IsNull / Null: 특정 필드가 null인 데이터를 조회
    // IsNotNull / NotNull: 특정 필드가 null이 아닌 데이터를 조회
    // StartingWith: 특정 문자열로 시작하는 데이터를 조회
    // EndingWith: 특정 문자열로 끝나는 데이터를 조회
    // Containing: 특정 문자열을 포함하는 데이터를 조회
    // After: 특정 날짜나 시간 이후의 데이터를 조회
    // Before: 특정 날짜나 시간 이전의 데이터를 조회
    // True: Boolean 필드가 true인 데이터를 조회
    // False: Boolean 필드가 false인 데이터를 조회
    // Exists: 해당 조건을 만족하는 데이터가 존재하는지 확인
    // Distinct: 중복된 결과를 제거하고 고유한 데이터만 조회
    // Top / First: 결과 중 상위 몇 개의 데이터를 조회
    // Count: 특정 조건에 해당하는 데이터의 개수를 셈
}
