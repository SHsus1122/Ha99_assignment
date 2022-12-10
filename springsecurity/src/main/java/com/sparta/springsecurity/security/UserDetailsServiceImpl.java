package com.sparta.springsecurity.security;

import com.sparta.springsecurity.entity.User;
import com.sparta.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 이 아래의 과정들은 (검증)
// DB 에서 유저를 조회하고 인증한 다음 UserDetails 를 반환하고 이를 사용해서 인증 객체를 만드는 과정이다.
// 역할 : DB 에 접근해서 user 객체를 가지고 온 다음 검증을 한다.

// UserDetailsService 란
// 스프링 시큐리티에서 유저의 정보를 가져오는 인터페이스이다.
// 유저의 정보를 불러오기 위해서 구현해야 하는 인터페이스 이다.
// 내부에 메서드가 하나밖에 없는데 loadUserByUsername 이는 유저 정보를 불러와서 UserDetails 로 리턴한다.
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // UserRepository 를 통해 DB 에 연결
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("UserDetailsServiceImpl.loadUserByUsername : " + username);

        // 사용자를 DB 에서 조회 없을시 예외 처리
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 유저 객체와 이름, 비밀번호를 UserDetailsImpl 로 반환
        return new UserDetailsImpl(user, user.getUsername(), user.getPassword());
    }

}