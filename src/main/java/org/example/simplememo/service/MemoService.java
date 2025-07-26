package org.example.simplememo.service;

import lombok.AllArgsConstructor;
import org.example.simplememo.dto.MemoRequestDto;
import org.example.simplememo.entity.Memo;
import org.example.simplememo.repository.MemoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;

    /**
     * 메모 생성
     * @param request 생성할 메모의 내용
     * @return 생성된 메모
     */
    public Memo createMemo(MemoRequestDto request) {
        Memo memo = request.toEntity();
        return memoRepository.save(memo);
    }

    /**
     * 메모 단건 조회
     * @param memoId 조회할 메모의 ID
     * @return 조회된 메모
     */
    public Memo getMemo(Long memoId) {
        return memoRepository.findById(memoId)
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 메모 전체 조회
     * @return 메모 리스트
     */
    public List<Memo> getMemos() {
        return memoRepository.findAll();
    }

    /**
     * 메모 수정
     * @param memoId 수정할 메모의 ID
     * @param request 수정할 메모의 내용
     * @return 수정된 메모
     */
    public Memo updateMemo(Long memoId, MemoRequestDto request) {
        Memo memo = getMemo(memoId); // 메모 단건 조회 기능을 이용하여 메모가 데이터베이스에 실존하는지 확인
        memo.setTitle(request.getTitle());
        memo.setContent(request.getContent());
        return memoRepository.save(memo); // Repository의 save 메소드를 이용하여 수정된 메모를 데이터베이스에 저장
    }

    /**
     * 메모 삭제
     * @param memoId 삭제할 메모의 ID
     */
    public void deleteMemo(Long memoId) {
        Memo memo = getMemo(memoId); // 메모 단건 조회 기능을 이용하여 메모가 데이터베이스에 실존하는지 확인
        memoRepository.delete(memo); // Repository의 delete 메소드를 이용하여 메모를 데이터베이스에서 삭제
    }
}