package com.example.hackerthon.Login.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 로그아웃된 토큰을 블랙리스트에 보관합니다.
 */
@Service
public class TokenBlacklistService {
    // ConcurrentHashMap 기반의 Set 으로 동시성 문제 방지
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    /** 토큰을 블랙리스트에 추가 */
    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    /** 토큰이 블랙리스트에 있는지 조회 */
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
