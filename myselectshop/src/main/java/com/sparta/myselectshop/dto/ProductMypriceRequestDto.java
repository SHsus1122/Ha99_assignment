package com.sparta.myselectshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor      // 파라미터가 없는 기본 생성자를 생성해준다.
@AllArgsConstructor     // 모든 필드 값을 파라미터로 받는 생성자를 만들어 준다.
public class ProductMypriceRequestDto {
    private int myprice;
}