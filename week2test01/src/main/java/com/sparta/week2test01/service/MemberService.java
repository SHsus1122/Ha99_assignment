package com.sparta.week2test01.service;


import com.sparta.week2test01.dto.MemberRequestDto;
import com.sparta.week2test01.dto.MemberResponseDto;
import com.sparta.week2test01.entity.Member;
import com.sparta.week2test01.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    // 싱글톤 생성자
    private final MemberRepository memberRepository;

    // 한명 조회
    public MemberResponseDto findMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new NullPointerException("회원 상세 조회 실패")
        );
        MemberResponseDto responseDto = new MemberResponseDto(member);
        return responseDto;
    }

    // 전체 조회
    public List<MemberResponseDto> findAllMember() {
        List<Member> ListMember = memberRepository.findAll();
        return ListMember.stream()
                .map(member -> new MemberResponseDto(member))
                .collect(Collectors.toList());
    }

    public MemberResponseDto createMember(MemberRequestDto requestDto) {
        Member member = new Member(requestDto);
        memberRepository.save(member);
        return new  MemberResponseDto(member);
    }
}