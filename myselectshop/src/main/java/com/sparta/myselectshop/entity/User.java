package com.sparta.myselectshop.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity(name = "users")     // 클래스를 엔티티로 선언. 이를 통해 JPA 가 해당 클래스를 관리한다.
                            // 쉽게 말해서 실체, 객체 라고 한다. 뒤의 name 부분은 엔티티의 이름을 정하는 용도
                            // @Table 이 없고 @Entity 만 존재하면 name 속성에 의해 엔티티와 Table 이름이 결정된다.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable : null 허용 여부
    // unique   : 중복 허용 여부 (false 일때 중복 허용)
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // @Enumerated 는 엔티티 클래스 속성 중 Enum 타입의 변수에 사용한다.
    // 선언된 상수의 이름을 String 클래스 타입으로 변환하여 DB 에 넣어준다.
    // 아래의 경우에는 Column 에 저장하는 형태이다.
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(String username, String password, String email, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

}