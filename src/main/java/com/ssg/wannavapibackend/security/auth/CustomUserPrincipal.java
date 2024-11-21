package com.ssg.wannavapibackend.security.auth;

import lombok.RequiredArgsConstructor;

import java.security.Principal;


// Lombok의 @RequiredArgsConstructor를 사용하여 final 필드의 생성자를 자동 생성
@RequiredArgsConstructor
public class CustomUserPrincipal implements Principal {

    // 사용자 ID 또는 고유 식별자를 저장하는 필드
    private final String mid;

    // Principal 인터페이스의 메서드 구현
    @Override
    public String getName() {
        // 사용자 식별자로 사용될 mid를 반환
        return mid;
    }
}