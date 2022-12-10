package com.sparta.springsecurity.config;


import com.sparta.springsecurity.security.CustomAccessDeniedHandler;
import com.sparta.springsecurity.security.CustomAuthenticationEntryPoint;
import com.sparta.springsecurity.security.CustomSecurityFilter;
import com.sparta.springsecurity.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 스프링 시큐리티(Spring Security)
 * 스프링 서버에 필요한 인증 및 인가를 위해 많은 기능을 제공
 * 개발의 수고를 덜어주며 마치 '스프링' 프레임워크가 웹 서버 구현에 편의를 제공하는 것과 같다.
 * build.gradle 에다가 아래의 코드를 추가하는 것으로 시작한다.
 * implementation 'org.springframework.boot:spring-boot-starter-security'
 */

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity                                      // 스프링 Security 기능 활성화
@EnableGlobalMethodSecurity(securedEnabled = true)      // @Secured 어노테이션 활성화 스프링 Bean 등록
public class WebSecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserDetailsServiceImpl userDetailsService;

    // PasswordEncoder 는 스프링 시큐리티의 인터페이스 객체
    // 비밀번호를 암호화 하는 역할로 구현체들이 하는 역할은 바로 이 암호화를 어떻게 할지, 암호화 알고리즘에 해당한다.
    // 따라서 PasswordEncoder 의 구현체를 대입해주고 스프링 빈으로 등록하는 과정이 필요하다.
    // BCryptPasswordEncoder 는 BCrypt 라는 해시 함수를 이용하여 패스워드를 암호화 한다.
    // BCrypt 는 적응형 단방향 함수 라고 한다.
    // 적응형 단방향 함수는 내부적으로 리소스 낭비가 심하기 때문에 API 요청 마다 이를 통해 검증하면 성능이 크게
    // 떨어질 수 있다. 그래서 세션, 토큰 과 같은 인증방식을 사용하여 검증하는 것이 속도 및 보안측면에 유리하다.

    /** 사용자 검증 흐름
     * 1. 회원가입 진행
     * 2. 사용자 정보 저장시 비밀번호 암호화
     * 3. 사용자가 로그인을 진행
     * 4. 사용자가 입력한 정보를 통해 저장된 암호화된 비밀번호를 가져와 사용자가 입력한 암호와 비교
     * 5. 인증이 성공하면 사용자의 정보를 사용해서 JWT 토큰을 생성해서 Header 에 추가해서 반환하고
     *    클라이언트는 이를 쿠키저장소에 저장
     * 6. 사용자는 게시글 작성과 같은 요청을 진행할 때 발급받은 JWT 토큰을 같이 보내고 서버는 이를
     *    빠르게 인증하고 사용자의 요청을 수행한다.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // webSecurityCustomizer 설정이 아래의
    // securityFilterChain 의 설정보다 더 우선적으로 걸린다.
    // WebSecurity 구성방법
    // WebSecurityCustomizer 를 Bean 으로 등록한다.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용 및 resources 접근 허용 설정
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        /** CSRF 설정
         * POST 요청마다 처래해주는 대신에 disable 로 해주면 된다.
         * 사이트간 요청 위조, 공격자 인증이 된 브라우저의 저장된 쿠키의 Session 정보를 활용하여
         * 웹 서버에 사용자가 의도하지 않은 요청을 전달하는 것을 의미한다.
         * 이것이 설정되어 있으면 HTML 에서 CSRF 토큰 값을 넘겨줘야 요청 수행이 가능
         * 쿠키 기반의 취약점을 이용한 공격이기 때문에 REST 방식의 API 에서 disable 이 가능하다.
         */
        http.csrf().disable();


        /** 접근 권한 설정
         * http.authorizeRequests()
         * 특정한 경로에 특정한 권한을 가진 사용자만 접근할 수 있도록 하는 메서드
         * antMatchers      : 특정한 경로를 지정한다.
         * permitAll()      : 해당 경로는 인증을 받지 않아도 누구나 접근이 가능해진다.
         * anyRequests()    : 다른 모든 요청들을 인증이나 권한 없이 허용한다.
         * authenticated()  : 애플리케이션에 로그인된 사용자가 요청을 수행할 때 필요하다.
         *                    만약 사용자기 인증되지 않았다면, 스프링 시큐리티 필터는 요청을 잡아내고
         *                    사용자를 로그인 페이지로 리다이렉션 해준다.
         * anyRequest().authenticated()
         * 앞의 URL 이외의 요청들은 모두 authenticated 인증 처리를 하겠다는 의미
         */
        http.authorizeRequests().antMatchers("/api/user/**").permitAll()
                .anyRequest().authenticated();


        /**  Custom 로그인 페이지 사용
         *  참고 링크 : https://velog.io/@seongwon97/Spring-Security-Form-Login
         *  http.formLogin() 스프링 시큐리티에서 제공하는 인증방식
         *  http.formLogin() 만 사용하면 default 로그인 방식으로 스프링에서 제공하는 기본 사이트가 뜬다.
         *  loginPage        로그인 페이지 지정
         */
        http.formLogin().loginPage("/api/user/login-page").permitAll();

        // Custom Filter 등록하기
        // http.addFilterBefore : 어떠한 필터 이전에 넣어준다(추가한다) 는 의미이다.
        //                        여기서는 커스텀한 SecurityFilter 를 사용하기 위해서 이 프로젝트 에서는
        //                        JWT 토큰을 사용할 것이기 때문에 JWT 토큰을 검증하는 필터를 만들어 추가할 것이다.
        //
        //                        즉, CustomSecurityFilter 를 UsernamePasswordAuthenticationFilter 이전에 실행할 수 있도록 설정해준다.
        //                        CustomSecurityFilter 를 통해서 인증 객체를 만들고 Context 에 추가하면 인증이 완료 되었기 때문에
        //                        다음 필터를 넘어가면서 컨트롤러 까지 요청이 갈 수 있게 된다.
        http.addFilterBefore(new CustomSecurityFilter(userDetailsService, passwordEncoder()), UsernamePasswordAuthenticationFilter.class);

        // 접근 제한 페이지 이동 설정
        // http.exceptionHandling().accessDeniedPage("/api/user/forbidden");

        // 401 Error 처리, Authorization 즉, 인증과정에서 실패할 시 처리
        // authenticationEntryPoint 는 인증이 되지 않은 유저가 요청을 헀을 때 동작한다.
        http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);

        // 403 Error 처리, 인증과는 별개로 추가적인 권한이 충족되지 않는 경우
        // accessDeniedHandler 는 서버에 요청을 할 때 엑세스가 가능한지 권한을 체크 후 엑세스 할 수 없는 요청을 했을 시 작동한다.
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);

        return http.build();
    }

}