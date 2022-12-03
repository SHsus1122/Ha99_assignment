package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 유저 ID 가 동일한 product 를 가져오는 메서드
    List<Product> findAllByUserId(Long userId);

    // product id 와 user id 가 일치하는 product 를 가져오는 메서드
    Optional<Product> findByIdAndUserId(Long id, Long userId);
}