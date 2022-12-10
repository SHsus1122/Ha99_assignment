package com.sparta.springsecurity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springsecurity.dto.SecurityExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // 객체 생성
    private static final SecurityExceptionDto exceptionDto =
            new SecurityExceptionDto(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
    // getReasonPhrase() 는 응답메시지의 문자열을 확인


    // 에러가 발생시 handle 을 통해서 클라리언트로 값을 반환한다.
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException{

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // Stream 이란 프로그램 외부에서 데이터를 읽거나 외부로 데이터를 출력하는 작업에서
        // 데이터가 이동하는 통로를 가리키는 말이다.
        // InputStream 과 OutputStream 이 있는데 여기서는 OutputStream 만 설명한다.
        // OutputStream 말 그대로 외부로 데이터를 출력하는 역할을 수행한다.
        // 바이트 단위의 입출력을 위한 최상위 입출력 스트림 클래스 이다.

        // ObjectMapper
        // JSON 객체를 직렬화 또는 역직렬화 시키기 위해 사용하는 Jackson 라이브러리의 클래스
        // 즉, 아래에서는 writeValue 를 통해서 역직렬화를 행했다.
        // JSON -> Java Object
        // flush() 를 통해서 버퍼에 들어있는 내용을 비워준다.. 자바에서는 출력해준다 라는 의미
        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, exceptionDto);
            os.flush();
        }
    }
}