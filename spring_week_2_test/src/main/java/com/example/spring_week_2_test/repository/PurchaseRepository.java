package com.example.spring_week_2_test.repository;

import com.example.spring_week_2_test.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
