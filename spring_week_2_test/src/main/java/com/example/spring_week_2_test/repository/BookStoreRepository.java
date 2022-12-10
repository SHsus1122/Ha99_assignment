package com.example.spring_week_2_test.repository;

import com.example.spring_week_2_test.entity.BookStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookStoreRepository extends JpaRepository<BookStore, Long> {
}
