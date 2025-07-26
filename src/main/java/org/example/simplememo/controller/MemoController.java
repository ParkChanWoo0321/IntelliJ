package org.example.simplememo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.simplememo.CommonResponse;
import org.example.simplememo.dto.MemoRequestDto;
import org.example.simplememo.dto.MemoResponseDto;
import org.example.simplememo.entity.Memo;
import org.example.simplememo.service.MemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@Tag(name= "Memo", description = "메모 관련 API")
@RestController
@AllArgsConstructor
@RequestMapping("/api/memo") //모든 메모 API의 기본 URL
public class MemoController {
    public  final MemoService memoService;
    /**
     * 메모 생성
     * @param request 생성할 메모의 내용
     * @return 생성된 메모
     */
    @Operation(summary = "메모 작성",description = "메모를 작성합니다.")
    @PostMapping
    public ResponseEntity<CommonResponse<MemoResponseDto>> memoCreate(@RequestBody MemoRequestDto request) {
        Memo memo = memoService.createMemo(request);
        MemoResponseDto response = new MemoResponseDto(memo);
        return ResponseEntity.ok()
            .body(CommonResponse.<MemoResponseDto>builder()
                .statusCode(200)
                .msg("메모 생성이 완료되었습니다.")
                .data(response)
                .build());
    }

    /**
     * 메모 단건 조회
     * @param memoId 조회할 메모의 ID
     * @return 조회된 메모
     */
    @GetMapping("/{memoId}")
    public ResponseEntity<CommonResponse<MemoResponseDto>> getMemo(@PathVariable Long memoId) {
        Memo memo = memoService.getMemo(memoId);
        MemoResponseDto response = new MemoResponseDto(memo);
        return ResponseEntity.ok()
                .body(CommonResponse.<MemoResponseDto>builder()
                        .statusCode(HttpStatus.OK.value())
                        .msg("단건 조회가 완료 되었습니다.")
                        .data(response)
                        .build());
    }

    /**
     * 메모 전체 조회
     * @return 메모 리스트
     */
    @GetMapping
    public ResponseEntity<CommonResponse<List<MemoResponseDto>>> getMemos() {
        List<Memo> memos = memoService.getMemos();
        List<MemoResponseDto> response = memos.stream()
                .map(MemoResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .body(CommonResponse.<List<MemoResponseDto>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .msg("목록 조회이 완료 되었습니다.")
                        .data(response)
                        .build());
    }

    /**
     * 메모 수정
     * @param memoId 수정할 메모의 ID
     * @param request 수정할 메모의 내용
     * @return 수정된 메모
     */
    @PutMapping("/{memoId}")
    public ResponseEntity<CommonResponse<MemoResponseDto>> updateMemo(
            @PathVariable Long memoId,
            @RequestBody MemoRequestDto request) {
        Memo memo = memoService.updateMemo(memoId, request);
        MemoResponseDto response = new MemoResponseDto(memo);
        return ResponseEntity.ok()
                .body(CommonResponse.<MemoResponseDto>builder()
                        .statusCode(HttpStatus.OK.value())
                        .msg("메모 수정이 완료되었습니다.")
                        .data(response)
                        .build());
    }

    /**
     * 메모 삭제
     * @param memoId 삭제할 메모의 ID
     */
    @DeleteMapping("/{memoId}")
    public ResponseEntity<CommonResponse<Void>> deleteMemo(@PathVariable Long memoId) {
        memoService.deleteMemo(memoId);
        return ResponseEntity.ok()
                .body(CommonResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .msg("메모 삭제가 완료되었습니다.")
                        .build());
    }
}