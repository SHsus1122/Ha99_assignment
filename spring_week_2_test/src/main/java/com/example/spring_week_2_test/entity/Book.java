package com.example.spring_week_2_test.entity;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 책 저자명
    @Column(nullable = false)
    private String author;

    // 책 이름
    @Column(nullable = false, unique = true)
    private String name;

    // 가격
    // 너무 오래된 책은 가격이 없는 경우가 있다.
    @Column(nullable = false)
    private int price;

    // 수량
    // 재고가 하나도 없을 수도 있다.
    @Column
    private long quantity;

    // 다대일 관계
    @ManyToOne
    @JoinColumn(name = "BOOKSTORE_ID")
    private BookStore bookStore;

    @ManyToOne
    @JoinColumn(name = "PURCHASE_ID")
    private Purchase purchase;

}
