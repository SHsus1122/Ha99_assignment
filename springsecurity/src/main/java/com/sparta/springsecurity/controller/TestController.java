package com.sparta.springsecurity.controller;

import com.sparta.springsecurity.entity.UserRoleEnum;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class TestController {

    // Admin 사용자만 이 요청을 허가해주는 형태
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping("/test-secured")
    public String securedTest(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("*********************************************************");
        System.out.println("UserController.securedTest");
        System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
        System.out.println("*********************************************************");

        return "redirect:/api/user/login-page";
    }
}