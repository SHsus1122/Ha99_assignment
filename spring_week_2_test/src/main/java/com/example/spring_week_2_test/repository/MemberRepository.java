package com.example.spring_week_2_test.repository;

import com.example.spring_week_2_test.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
