package com.sparta.myselectshop.entity;

// https://mine-it-record.tistory.com/204
// enum 자체의 의미는 "관련이 있는 상수들의 집합" 을 의미한다.
// 클래스처럼 보이게 하는 상수
// 서로 관련있는 상수들끼리 모아 상수들을 대표할 수 있는 이름으로 타입을 정의하는 것
// Enum 클래스 형을 기반으로 한 클래스형 선언

// 특징
// 열거형으로 선언된 순서에 따라 0부터 index 값을 가진다.(순차적으로 증가)
// 여기서 지정된 상수들은 모두 대문자로 선언하고 마지막에 세미콜론(;) 을 사용하지 않는다.
// 상수와 특정 값을 연결시킬 경우에만 마지막에 세미콜론을 붙여준다.

// 몇가지 한정된 데이터들을 가지는 경우에 이 열거형을 사용한다.

public enum UserRoleEnum {
    USER(Authority.USER),  // 사용자 권한
    ADMIN(Authority.ADMIN);  // 관리자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}