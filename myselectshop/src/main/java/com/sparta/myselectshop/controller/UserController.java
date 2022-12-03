package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.LoginRequestDto;
import com.sparta.myselectshop.dto.SignupRequestDto;
import com.sparta.myselectshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 회원가입 페이지로 연결
    @GetMapping("/signup")
    public ModelAndView signupPage() {
        // Model 은 객체를 파라미터로 받아서 데이터를 뷰로 넘기는 역할을 하는데
        // ModelAndView 도 같으나 데이터와 뷰를 동시 설정이 가능하다는 차이가 있다.
        return new ModelAndView("signup");
    }

    // 로그인 페이지로 연결
    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }

    // 회원가입 진행
    // 회원가입 페이지에서 회원가입을 진행하면, signupRequestDto 에 클라가 입력한 데이터를 받아온다.
    // 그 데이터를 들고 서비스의 signup 메서드로 가서 진행한다.
    // 이후에 redirect 를 이용해서 로그인 페이지로 돌려보낸다.
    @PostMapping("/signup")
    public String signup(SignupRequestDto signupRequestDto) {
        userService.signup(signupRequestDto);
        return "redirect:/api/user/login";
    }

    // 로그인 진행
    // 값 자체를 받아오는 ResponseBody 를 사용
    // 위에서는 Form 태그로 넘어오면서 ModelAttribute 형식으로 받아와서 RequestBody 가 필요하지 않았다.
    // ajax 에서 body 값이 넘어오기 때문에 RequestBody 가 필요하다.
    // HTTPRequest 에서 header 가 넘어와서 받아오는 것 처럼 클라 쪽으로 반환할 때 Response 객체를 반환한다.
    // 반환 할 response 의 header 에다가 만들어 준 Token 을 넣어주기 위해서 사용
    @ResponseBody
    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        userService.login(loginRequestDto, response);
        return "success";
    }
}