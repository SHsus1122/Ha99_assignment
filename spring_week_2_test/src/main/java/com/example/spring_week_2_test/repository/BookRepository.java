package com.example.spring_week_2_test.repository;

import com.example.spring_week_2_test.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
