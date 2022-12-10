package com.example.spring_week_2_test.entity;

import jakarta.persistence.*;

@Entity
public class Member {
    @Id
    @Column(name = "Member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저의 주소
    // 상세주소로 빠지면 유니크를 걸 수 있지만
    // 큰 주소 개념으로 빠지면 걸 수 없다.
    // 그리고 같은 집에서 다수의 사람이 회원가입을 할 수도 있다.
    // nullable = false 의 경우 회원가입시 주소 입력이 강제가 안되는 경우도 있다.
    @Column
    private String address;

    // 유저의 이메일
    @Column(nullable = false, unique = true)
    private String email;

    // 유저의 이름
    @Column(nullable = false, unique = true)
    private String nickname;

    // 유저의 암호
    @Column(nullable = false)
    private String password;

    // 유저의 핸드폰 번호
    // 인증을 안 하는 경우도 있기 때문에 null 값이 가능
    @Column(unique = true)
    private String phoneNumber;

    // 책 구매 카운트
    @ManyToOne
    @JoinColumn(name = "PURCHASE_ID")
    private Purchase purchase;

}
