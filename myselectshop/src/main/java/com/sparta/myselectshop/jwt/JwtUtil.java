package com.sparta.myselectshop.jwt;


import com.sparta.myselectshop.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

// @Slf4j 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음이다.
// 로깅이 필요한 부분에 log 변수로 로그를 생성한다. 디버깅이 목적이다.
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    // 클라이언트가 username 과 password 로 로그인 성공하면
    // 로그인 정보 를 JWT 로 암호화(secret key 사용) 해서 Token 을 반환한다.
    // 그러고나서 JWT 를 클라이언트에 전달한다.
    // 클라이언트 이렇게 넘어온 JWT 를 쿠키 저장소나 local 저장소에 저장한다.

    /**
     * Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0amd1cnRuMTEyMiIsImF1dGgiOiJVU0VSIiwiZXhwIjoxNjcwMDkzOTI1LCJpYXQiOjE2NzAwOTAzMjV9.N4YGOYHpDh64sJocfT_QXUJpF-nZz4P-8BTJ-hZ2_AA
     * 위 같은 형태로 쿠키 안에 들어간다.
     * 이 때, 이름이 Authorization 으로 들어가고(즉, key 값을 의미한다)
     * 안의 내용이 "Bearer " 문자열을 보면 한 칸 띄우고 시작한다.
     * 토큰을 만들 때 같이 앞에 붙어서 들어간다.
     */
    // Header KEY 값
    // Authorization(권한 부여)
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // 사용자 권한 값의 KEY
    // 여기에 enum 열거형 클래스의 사용자 권한이 들어간다.
    public static final String AUTHORIZATION_KEY = "auth";

    // Token 식별자
    private static final String BEARER_PREFIX = "Bearer ";

    // 토큰 만료시간
    // 밀리초를 기준으로 계산한다. 식은 잘 모르겠는데 아래의 경우 1시간 이다.
    private static final long TOKEN_TIME = 60 * 60 * 1000L;

    // 아래의 secret.key 가 jwt 토큰을 풀기 위한 암호 키 마우스를 올려보면 값이 나온다.
    // @Value 어노테이션을 사용하면 실행부인 어플리케이션의 키값을 가져올 수 있다.
    // @Value("${jwt.secret.key}") 이러한 형태로 선언
    @Value("${jwt.secret.key}")
    private String secretKey;

    // 토큰을 만들 때 넣어줄 키값으로 secret key 가 들어간다.
    private Key key;
    //
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // @PostConstruct 처음에 객체가 생성될 때 초기화 하는 함수
    // 의존성 주입이 이루어진 후 초기화를 수행하는 메서드이다. 서비스(로직 수행 전) 시작전에 발생한다.
    // 이는 생성자보다 늦게 호출되며 다른 리소스에서 호출되지 않아도 수행한다.
    @PostConstruct
    public void init() {
        // 시크릿키를 Base64 로 인코딩 했기 때문에 디코딩도 Base64 로 디코딩 한다.
        // key 객체에 만들어진 byte 값을 넣어준다. 토큰 만들 때 넣어준다.
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기
    // HttpServletRequest 객체 안에 가져와야 할 토큰이 header 에 들어있고 request.getHeader 을 통해 들고온다.
    // 그 안에 파라미터로 어떠한 key 를 가져올지 넣어주면 된다. 위에서는 AUTHORIZATION_HEADER 로 지정했다.
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        // 위에서 가지고 온 key 가 있는지 확인을 해준다. StringUtils.hasText(bearerToken) 이부분
        // 있다는 조건이 성립되고 bearerToken.startsWith(BEARER_PREFIX) 이 부분 인데
        // 시작 부분이 위에서 지정한 Bearer 으로 시작하는지 판별한다.
        // substring 을 사용해서 앞의 7 글자를 지워준다. 이렇게 하는 이유는 위에서 선언한 토큰 시작부에
        // Bearer 뒤에 공백을 하나 붙여줬기 때문에 앞의 7 글자를 날려준다.
        // 위 과정을 거쳐서 순수 토큰 값만을 남겨서 반환을 한다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 생성
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        /**
         * 1. 앞부분에 "Bearer " 이 붙어있기 때문에 시작부에 식별자로서 붙여주고 시작
         * 2. setSubject 는 공간을 의미하고 해당 부분에 username 을 넣어준다.
         * 3. claim 이라는 공간에는 이곳에 사용자의 권한을 넣어준다.
         *    이 권한을 가져올 때는 앞에서 지정한 auth key 를 사용해서 넣는다.
         * 4. setExpiration 에는 토큰의 유효 기간을 지정해서 넣어준다.
         * 5. Date 의 기능을 이용해서 date.getTime() 으로 현재 시간을 가져온다.
         *    이후에 뒤에 위에서 지정한 시간을 더해서 생성시간 기준 한시간을 추가
         * 6. setIssuedAt 는 토큰이 만들어지 시점을 넣어준다.
         * 7. signWith 가 중요한 부분이다. 이 부분이 실제로 위에서 secret key 를 사용해서 만든
         *    key 객체와 해당 key 객체를 어떠한 알고리즘을 사용해서 암호화 할것인지 지정하는 부분이다.
         *    위에서는 HS256 을 사용해서 암호화 해줬다.
         * 8. compact 를 통해서 String 형식의 JWT 토큰으로 반환이 된다.
         */
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // 토큰 검증
    // Jwts 에 들어있는 것을 parserBuilder 를 통해 검증을 한다.
    // setSigningKey 에다가 토큰 생성시 사용한 key 를 넣어준다.
    // parseClaimsJws 어떠한 토큰을 검증할 것인지를 파라미터로 넣어주면 내부적으로 검증을 해준다.
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    // 위의 검증식과 일치하나 마지막에 getBody 를 통해서 안에 들어있는 값을 가져온다.
    // 앞의 validateToken 부분에서 이미 검증을 거쳤기 때문에 따로 검증을 거치지 않고 바로 넣어준다.
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

}