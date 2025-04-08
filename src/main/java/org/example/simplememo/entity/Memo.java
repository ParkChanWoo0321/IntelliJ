package org.example.simplememo.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "memo_id", nullable = false)
    private Long memoId;

    private String title;

    private String content;

    @Builder
    public Memo(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
