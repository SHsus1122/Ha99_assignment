package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.LoginRequestDto;
import com.sparta.myselectshop.dto.SignupRequestDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 관리자 계정 생성시 필요한 암호
    // ADMIN_TOKEN
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    // 회원 가입 처리 부분
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();   // username 변수에 유저가 입력한 값 가져와서 담기
        String password = signupRequestDto.getPassword();   // password 변수에도 똑같이 작업

        // 회원 중복 확인
        // DB 에 접근해서 클라이언트가 입력한 username 과 동일한 녀석이 있는지 찾는다.
        // isPresent 메소드가 Optional 객체가 값을 가지고 있다면 true, 없다면 false 를 리턴한다.
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        String email = signupRequestDto.getEmail();
        // 사용자 ROLE 확인
        // 기본적으로 유저의 권한 부분에 일반 USER 로 지정한다.
        UserRoleEnum role = UserRoleEnum.USER;
        // boolean 타입의 경우에는 getter 나 setter 에서 get, set 으로 호출이 아닌 is 가 앞에 붙은 형태로 호출한다.
        // 회원가입을 하는 유저의 관리자 권한을 판별하는 부분
        // 만약 유저가 어드민 권한부분을 체크하고 코드를 입력하면 판별하는 부분이다.
        if (signupRequestDto.isAdmin()) {
            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 위의 모든 과정이 잘 통과 되면 이제 새로운 객체를 만들어서 값을 넣어주고
        // save 함수를 이용해서 새롭게 DB 에 전달해서 등록완료
        User user = new User(username, password, email, role);
        userRepository.save(user);
    }


    // 로그인 처리 부분
    @Transactional(readOnly = true)
    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        // orElseThrow 는 Optional 의 인자가 null 일 경우 예외처리를 한다.
        // user 변수에 DB 에서 입력한 유저의 이름을 찾아서 가져오는 것이 기본이다.
        // 여기서 추가로 orElseThrow 를 사용해서 검증을 거치는 것도 추가해 준다.
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        // 비밀번호 확인
        // 위에서 이미 user 에다가 가져온 정보에 비밀번호 확인작업을 거친다.
        if(!user.getPassword().equals(password)){
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // addHeader 함수는 클라이언트로의 응답 중 헤더부분에 원하는 내용을 추가해주는 함수
        // 이를 이용해서 JwtUtil 의 토큰을 가져와서 넣어준다.
        //
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getRole()));
    }
}
