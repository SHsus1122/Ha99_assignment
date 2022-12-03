package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController                 // 기존 컨트롤러에 ResponseBody 가 결합된 어노테이션
                                // 즉, 하위 메서드에 ResponseBody 가 없어도 문자열과 JSON 전송이 가능하다.
@RequestMapping("/api")     // 기본 url 을 localhost:8080/api 이런식으로 고정 즉 모든 url 의 시작을 지정
@RequiredArgsConstructor       // final 이나 @NotNull 이 붙은 필드의 생성자를 자동으로 생성해주는 롬복 어노테이션
public class ProductController {

    private final ProductService productService;
    // RequiredArgsConstructor 이 없으면 아래와 같은 형태로 생성자 주입을 해 주어야만 한다.
/*
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
*/


    // 관심 상품 등록하기
    // @PostMapping 스프링 4.3 부터 등장한 어노테이션 HTTP 메서드로 매핑시키는 방법을 더 간단하게 지정 가능하다.
    // 참고 링크 : https://change-words.tistory.com/entry/Spring-%EC%8A%A4%ED%94%84%EB%A7%81-RequestMapping-%EB%8C%80%EC%8B%A0-PostMapping-GetMapping-%EC%93%B0%EB%8A%94-%EC%9D%B4%EC%9C%A0
    // @RequestMapping(value = "/products", method = RequestMethod.POST)
    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, HttpServletRequest request) {
        // 리턴 타입 즉, 결과의 반환 타입은 ProductResponseDto 로 지정
        // 매개 변수 requestDto 에 클라이언트가 입력한 JSON 값이 들어간다. request 에는 토큰 정보가 들어간다.
        // productService.createProduct 여기에 위에서 가져온 값들을 넣어서 서비스로 전달
        /**
         * HttpServletRequest : JSP 기본 내장 객체
         * 클라이언트로 부터 서버로 요청이 들어오면 서버에서는 HttpServletRequest 를 생성하며, 요청 정보에 있는
         * 패스로 매핑된 서블릿에게 전달한다. 이 후 전달받은 내용들을 파라미터로 Get과 Post 형식으로 클라에게 전달한다.
         * 즉, 이를 이용해서 값을 받아오며 회원 정보를 컨트롤러로 보냈을 때 HttpServletRequest 객체 안에 모든 데이터가 들어간다.
         * requestDto 가 있는데 이걸 사용하는 이유는 request Header 안에 들어있는 Token 값을 가져오기 위해서다.
         */
        // 응답 보내기
        return productService.createProduct(requestDto, request);
    }

    // 관심 상품 조회하기
    @GetMapping("/products")
    public List<ProductResponseDto> getProducts(HttpServletRequest request) {
        // 응답 보내기
        return productService.getProducts(request);
    }

    // 관심 상품 최저가 등록하기
    // PathVariable : URL 경로에 변수를 넣는다. 이를 이용해 관싱 상품의 정확한 id 주소를 찾아온다.
    // 내가 직접 지정하는 가격을 가지는 ProductMypriceRequestDto 에다가 입력값을 받아서 가져온다.
    // 토큰을 가져와서 검증해서 해당 제품을 업데이트를 진행한다.(최저가 등록)
    @PutMapping("/products/{id}")
    public Long updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto, HttpServletRequest request) {
        // 응답 보내기 (업데이트된 상품 id)
        return productService.updateProduct(id, requestDto, request);
    }

}