package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.ProductRepository;
import com.sparta.myselectshop.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 관심 상품 추가 부분
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 관심상품 추가 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            // 요청받은 DTO 로 DB에 저장할 객체 만들기
            // product 에 userid 가 들어가는데 이를 말로하면 product 와 user 가 연관관계가 있다고 한다.
            // 결론은 새롭게 만들어지는 product 에 만들 때 userid 를 같이 넣어준다.
            Product product = productRepository.saveAndFlush(new Product(requestDto, user.getId()));

            return new ProductResponseDto(product);
        } else {
            return null;
        }
    }


    // 관심 상품 조회 부분
    // 아래의 코드가 긴 이유는 토큰을 통해서 검증이 된 유저만 조회가 가능하게 되어있다.
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProducts(HttpServletRequest request) {
        // Request 에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);

        // Claims 는 JWT 안에 들어있는 정보들을 담을 수 있는 객체
        Claims claims;

        // 토큰이 있는 경우에만 관심상품 조회 가능
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            // claims.getSubject() 이 안에 유저의 이름이 들어있다.
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            // 사용자 권한 가져와서 ADMIN 이면 전체 조회, USER 면 본인이 추가한 부분 조회
            // 위의 user 에서 가져온 권한을 userRoleEnum 에 담아준다.
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);

            // 반환을 하기 위한 List 를 새롭게 생성해준다.
            List<ProductResponseDto> list = new ArrayList<>();
            List<Product> productList;

            if (userRoleEnum == UserRoleEnum.USER) {
                // 사용자 권한이 USER일 경우
                // 유저의 아이디와 동일한 상품만을 가져와서 담아준다.
                productList = productRepository.findAllByUserId(user.getId());
            } else {
                // admin 계정이면 전부 다 가져와준다.
                productList = productRepository.findAll();
            }

            // 가지고온 product 들을 list 에 담아서 반환한다.
            for (Product product : productList) {
                list.add(new ProductResponseDto(product));
            }

            return list;

        } else {
            return null;
        }
    }


    // 관심 상품 최저가
    @Transactional
    public Long updateProduct(Long id, ProductMypriceRequestDto requestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 관심상품 최저가 업데이트 가능
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            // 위에서 product 에 userid 가 추가되었기 때문에 아래의 과정이 필요하다.
            // 현재 로그인한 유저가 선택한 product 가 맞는지 확인하는 과정이다.
            Product product = productRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                    () -> new NullPointerException("해당 상품은 존재하지 않습니다.")
            );

            product.update(requestDto);

            return product.getId();

        } else {
            return null;
        }
    }

    @Transactional
    public void updateBySearch (Long id, ItemDto itemDto){
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 상품은 존재하지 않습니다.")
        );
        product.updateByItemDto(itemDto);
    }

}