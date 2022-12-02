package com.sparta.myselectshopbeta.naver.service;

import com.sparta.myselectshopbeta.naver.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

// Slf4j : 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음
//         말이 어려운 쉽게 말해 좀 더 편리하게 로그를 찍기 위해서 사용한다.
//         Logger 객체 생성 없이 바로 log.debug() 로 로그를 찍어볼 수 있다.
@Slf4j
@Service
public class NaverApiService {

    public List<ItemDto> searchItems(String query) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "vtalOavotXL_c2AI8i1r");
        headers.add("X-Naver-Client-Secret", "xScIj5LZ8h");
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?display=15&query=" + query , HttpMethod.GET, requestEntity, String.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        log.info("NAVER API Status Code : " + status);

        String response = responseEntity.getBody();

        return fromJSONtoItems(response);
    }

    // Dto 변환부
    // 시작할 때 설치한 json 패키지에 들어있는 JSONObject, JSONArray 를 사용해서 반환을 한다.
    public List<ItemDto> fromJSONtoItems(String response) {

        // 포스트맨에서 봤던 json 데이터를 JSONObject rjson 변수에 넣는다.
        // getJSONArray 를 사용해서 rjson 에서 items 부분만 뽑아와서 items 에 넣어준다.
        JSONObject rjson = new JSONObject(response);
        JSONArray items  = rjson.getJSONArray("items");
        List<ItemDto> itemDtoList = new ArrayList<>();

        // 위에서 받아오는 최대 갯수를 15개로 지정했기 때문에 안에 담긴 총 15개의 items 데이터를
        // items 길이 만큼 반복문을 돌려서 새로운 배열에 넣어준다.
        // 과정은 받아온 값을 itemDto 에 넣어서 변환을 해주고 마지막으로 itemDto 넣어서 리턴
        for (int i=0; i<items.length(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            ItemDto itemDto = new ItemDto(itemJson);
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }
}