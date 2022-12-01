package com.sparta.week2test01.controller;


import com.sparta.week2test01.dto.MemberRequestDto;
import com.sparta.week2test01.dto.MemberResponseDto;
import com.sparta.week2test01.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 한 회원의 userId가 주었을때 회원 정보를 조회하는 API
     * 올라가는지 테스트
     */
    @GetMapping("/member/{id}")
    public MemberResponseDto getMemberInfo(@PathVariable Long id) {
        return memberService.findMember(id);
    }

    /**
     * 회원의 전체 목록을 조회하는 API
     */
    @GetMapping("/member")
    public List<MemberResponseDto> getMemberList() {
        return memberService.findAllMember();
    }

    @PostMapping("/member")
    public MemberResponseDto createMember(@RequestBody MemberRequestDto requestDto) {
        return memberService.createMember(requestDto);
    }
}