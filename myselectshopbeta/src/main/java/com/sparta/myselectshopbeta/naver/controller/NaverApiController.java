package com.sparta.myselectshopbeta.naver.controller;


import com.sparta.myselectshopbeta.naver.dto.ItemDto;
import com.sparta.myselectshopbeta.naver.service.NaverApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")     // 컨트롤러에서 들어오는 모든 요청들은 /api 로 받는다.
@RequiredArgsConstructor        // 의존성 자동 주입
public class NaverApiController {

    private final NaverApiService naverApiService;

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String query)  {
        return naverApiService.searchItems(query);
    }
}