package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// [7주차 연습문제 ②] 오류 처리 한 곳에 모으기
// @RestController 가 붙은 API 컨트롤러에서 발생한 예외만 처리한다 (화면 컨트롤러는 기존 방식 유지)
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class) // 중복 아이디, 빈 값, 허용 안 되는 권한, 본인 변경 …
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // 400 {"error": "..."}
    }
}
