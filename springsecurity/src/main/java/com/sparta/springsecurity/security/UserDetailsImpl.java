package com.sparta.springsecurity.security;


import com.sparta.springsecurity.entity.User;
import com.sparta.springsecurity.entity.UserRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 인증이 완료된 사용자를 추가하는 과정이다.
// 역할 : UserDetailsServiceImpl 에서 검증을 해서 가지고 온  user 의 정보를 활용해서 만든 객체를 만듬

// UserDetails 란?
// https://programmer93.tistory.com/68
// 스프링 시큐리티에서 사용자의 정보를 담는 인터페이스 이다.
// 사용자의 정보를 불러오기 위해서 구현해야 하는 인터페이스로 기본 오버라이드 메서드들을 이용한다.
public class UserDetailsImpl implements UserDetails {

    private final User user;        // 인증완료된 User 객체
    private final String username;  // 인증완료된 User의 ID
    private final String password;  // 인증완료된 User의 PWD

    // 생성자를 이용해서 초기화 !
    public UserDetailsImpl(User user, String username, String password) {
        this.user = user;
        this.username = username;
        this.password = password;
    }

    //  인증완료된 User 를 가져오는 Getter
    public User getUser() {
        return user;
    }

    /** 아래에서 사용된 제네릭(Generic)에 대한 정리
     *  https://st-lab.tistory.com/153
     * 직역하자면, "일반적인" 이라는 의미를 가진다.
     * 데이터 형식에 의존하지 않고, 하나의 값이 여러 다른 데이터 타입들을 가질 수 있도록 하는 방법이다.
     * 일반적인 유형 : <타입>객체명<타입>();
     * <> 이 괄호 안에 들어가는 타입을 지정해준다.
     *
     * 상황으로 예를들면, 어떤 자료구조를 만들어 배포하려고 한다.
     * 그런데 String 타입도 Integer 타입도 지원하는 등 많은 타입을 지원하고 싶을 때 하나하나 만들지
     * 않고도 가능하게 하는 효율적인 방법이다.
     *
     * 이렇듯, 제네릭은 클래스 내부에서 지정하는 것이 아닌 외부에서 사용자에 의해 지정되는 것을 의미한다.
     * <T> : Type  /  <E> : Element  /  <K> : Key  /  <V> : Value  /  <N> : Number
     */

    // 사용자의 권한 GrantedAuthority 로 추상화 및 반환
    // Collection 은 데이터의 집합, 그룹을 의미하며, JCF 는 이러한 데이터 자료구조인 컬렉션과
    // 이를 구현하는 클래스를 정의하는 인터페이스를 제공한다. (즉, 인터페이스 기반)
    // List 와 Set 의 상위 개념
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // GrantedAuthority 는 현재 사용자가 가지고 있는 권한을 의미한다.

        // 유저의 권한을 가져와서 스트링으로 변환해준다.
        UserRoleEnum role = user.getRole();
        String authority = role.getAuthority();

        // 추상화 해서 사용
        // SimpleGrantedAuthority 을 통해 권한 설정을 해준다.
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);

        // Collection 을 사용
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }



    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}