package com.sparta.myselectshopbeta.naver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Getter
@NoArgsConstructor
public class ItemDto {
    private String title;
    private String link;
    private String image;
    private int lprice;

    // JSONObject 를 파라미터 값으로 받고,
    public ItemDto(JSONObject itemJson) {
        // getString 쪽에 키값을 넣으면 그 값을 가지고 온다.
        // 즉 실제로 JSON 안에 있는 Key 값을 getString 뒤에 넣어준다.
        // 이후 최종적으로 위으 Dto 변수에 넣어준다.
        this.title = itemJson.getString("title");
        this.link = itemJson.getString("link");
        this.image = itemJson.getString("image");
        this.lprice = itemJson.getInt("lprice");
    }
}