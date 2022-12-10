package com.sparta.springsecurity.repository;


import com.sparta.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository 를 이용해서 우선 레포를 JPA 와 연결해준다.
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

}