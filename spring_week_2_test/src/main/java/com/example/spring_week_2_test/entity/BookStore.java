package com.example.spring_week_2_test.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BookStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 서점 위치
    // 같은 건물에 상세주소가 다른 경우가 존재
    // 이 주소에 상세주소까지 들어가면 같으나 보통 다르게 분류하니 유니크 제거
    @Column(nullable = false)
    private String location;

    // 서점 이름
    // 같은 이름의 서점에 무슨무슨 지점으로 분류하니 유니크 제거
    @Column(nullable = false)
    private String name;

    // Book과 양방향 연관관계
    // mappedBy 를 이용해서 관계의 주인이 누군지 명시
    @OneToMany(mappedBy = "bookStore")
    private List<Book> books = new ArrayList<>();

    // 유저와 단방향 연관관계
    @OneToMany
    @JoinColumn(name = "BOOKSTROE_ID")
    private List<Member> members = new ArrayList<>();
}
