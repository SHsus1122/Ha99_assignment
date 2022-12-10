package com.sparta.springsecurity.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomSecurityFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;


    /** CustomSecurityFilter 사용법 및 설명
     * https://mangkyu.tistory.com/14 , https://pgnt.tistory.com/102
     * https://minkukjo.github.io/framework/2020/12/18/Spring-142/
     * https://dololak.tistory.com/502
     * 클래스 부분을 설명하면 기존에 스프링이 가지고 있는 필터(OncePerRequestFilter)를 상속을 받아서 새롭게
     * CustomSecurityFilter 라는 필터를 만든다는 의미이다.
     *
     * OncePerRequestFilter 에 대해서 설명을 하자면..
     *      모든 서블릿에 일관된 요청을 처리하기 위해 만들어진 필터이다. 즉, 사용자의 한번에 요청 당 한번만 실행
     *      문제라면,
     *      서블릿이 다른 서블릿으로 dispatch(보내다) 되는 경우가 있을 수 있다.
     *      가장 대표적으로 스프링 시큐리티에서 인증과 접근 제어 기능이 필터로 구현되어진다.
     *      이러한 인증과 접근제어는 RequestDispatcher 클래스에 의해 다른 서블릿으로 dispatch 되는데,
     *      이 때 이동할 서블릿에 도착하기 전에 다시 한번 filter chain 을 거치게 된다.
     *      이 때, 또 다른 서블릿이 우리가 정의해둔 필터가 filter 나 GenericFilterBean 로 구현된 filter 를
     *      또 타면서 필터가 두 번 실행되는 현상이 발생할 수 있다.
     *      즉, 이같은 경우를 해결하기 위헤서 사용되는 것이 OncePerRequestFilter 이다.
     *
     * GenericFilterBean 란 ??
     *      기존 필터에서 얻어올 수 없는 정보였던 Spring 설정 정보를 가져오는 추상 클래스 이다.
     *
     * RequestDispatcher 란 ??
     *      클라이언트로 부터 최초에 들어온 요청을 JSP/서블렛 내에서 원하는 자원으로 요청을 넘기는
     *      역할을 수행하거나, 특정 자원에 처리를 요청하고 처리 결과를 얻어오는 기능을 수행하는 클래스
     *
     * 그 전에 Filter 란 ??
     *      서블릿의 ServletContext 기능으로 사용자에 의해 서블릿이 호출 되기 전/후로
     *      사용자 요청/응답의 헤더 정보 등을 검사 및 설정할 수 있다.
     *
     * 그렇다면 서블릿이란 ??
     *      쉽게 말해서...
     *      사용자의 요청을 받으면 서블릿을 생성해 메모리에 저장해두고, 같은 클라이언트의 요청을 받으면 생성해둔
     *      서블릿 객체를 재활용하여 요청을 처리한다.
     *      클라이언트의 요청을 처리하고, 그 결과를 반환하는 Servlet 클래스의 구현 규칙을 지킨 자바 웹 프로그래밍 기술
     *      즉, 자바를 사용하여 웹을 만들기 위해 필요한 기술로 클라이언트가 어떠한 요청을 하면 그에 대한 결과를 다시
     *      전송해주어야 하는데, 이런한 역할을 하는 자바 프로그램이다.
     */


    // 위에서 상속을 받은 필터를 doFilterInternal 함수를 Override 해서 재정의 해서 사용한다.
    // 파라미터로 3개의 값을 받아온다.
    // 우선 request 에 관해서..
    // API 요청이 오면 HTTP 객체가 Filter 를 타고 Controller 까지 오는데, UserDetailsServiceImpl 또한 필터이기 때문에
    // 들어오는 request 즉, HTTP 객체를 파라미터로 받는 것이다.
    // FilterChain 에 관해서..
    // 필터가 체인형식으로 연결되어 있는 이것을 사용해서 필터끼리 이동을 한다. 다음 필터로 이동을 해야하는데
    // 그래서 메서드 마지막에 있는 filterChain.doFilter 를 사용해서 request 와 response 를 담아서 다음 필터로 이동한다.
    // 만약 예외처리가 발생하면 그 이전 필터로 예외가 넘어간다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // getParameter 를 사용하면 키값으로 클라이언트 쪽에서 넘어오는 값을 가져올 수 있다.
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("username = " + username);
        System.out.println("password = " + password);
        System.out.println("request.getRequestURI() = " + request.getRequestURI());

        // 조건이 유저이름 또는 비밀번호가 null 이 아니고, if 문 안의 URL 로 요청이 왔을 때 수행한다.
        if(username != null && password  != null && (request.getRequestURI().equals("/api/user/login") || request.getRequestURI().equals("/api/test-secured"))){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 비밀번호 확인
            if(!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new IllegalAccessError("비밀번호가 일치하지 않습니다.");
            }

            // 인증 객체 생성 및 등록
            // SecurityContext : Authentication(인증 객체)가 저장되는 보관소, 현재 인증한 user 에 대한 정보를 가지고 있다.
            // Authentication  : 사용자의 인증 정보를 저장하는 토큰 개념
            //                   주로 인증시 id 와 password 를 담고 인증 검증을 위해 전달되어 사용된다.
            //                   인증 후 최종 인증 결과(user 객체, 권한정보) 를 담고 SecurityContext 에 저장되어 아래의
            //                   Authentication authentication 으로 시작해서 전역적으로 참조가 가능하다.
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            // principal   :  사용자를 식별한다.  Username/Password 방식으로 인증할 때 보통 UserDetails 인스턴스다.
            // credentials : 주로 비밀번호, 대부분 사용자 인증에 사용하고 다음 비운다.
            // authorities : 사용자에게 부여한 권한을 GrantedAuthority 로 추상화하여 사용한다.
            // 아래의 credentials 에는 위에서 이미 비밀번호 검증을 거쳤기 때문에 null 을 넣음
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // context 안에 authentication 를 넣고
            context.setAuthentication(authentication);

            // context 를 ContextHolder 에 넣는다.
            // SecurityContextHolder 는 누가 인증 했는지에 대한 정보들을 저장하고 있다.
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request,response);
    }
}