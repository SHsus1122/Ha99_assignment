package com.example.spring_week_2_test.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Purchase {

    // 책 구매
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 맴버의 책 구매 카운트
    @OneToMany(mappedBy = "purchase")
    private List<Member> members = new ArrayList<>();

    // 책 재고 카운트
    @OneToMany(mappedBy = "purchase")
    private List<Book> books = new ArrayList<>();
}
