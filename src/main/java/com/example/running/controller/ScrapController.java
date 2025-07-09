package com.example.running.controller;

import com.example.running.dto.ScrapPostDto;
import com.example.running.dto.ScrapRequestDto;
import com.example.running.dto.ScrapResponseDto;
import com.example.running.service.ScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scraps")
public class ScrapController {

    private final ScrapService scrapService;

    public ScrapController(ScrapService scrapService) {
        this.scrapService = scrapService;
    }

    // 글 스크랩 , 스크랩 취소
    @PostMapping
    public ResponseEntity<ScrapResponseDto> toggleScrap(@RequestBody ScrapRequestDto requestDto) {
        ScrapResponseDto responseDto = scrapService.toggleScrap(requestDto.getUserId(), requestDto.getPostId());
        return ResponseEntity.ok(responseDto);
    }

    // 스크랩 글 목록 조회
    @GetMapping("/users/{userId}/scraps")
    public ResponseEntity<List<ScrapPostDto>> getScrapList(@PathVariable Long userId) {
        List<ScrapPostDto> scrapList = scrapService.getScrapList(userId);
        return ResponseEntity.ok(scrapList);
    }
}
