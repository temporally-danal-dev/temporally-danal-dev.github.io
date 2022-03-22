package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class SubmitResponse {
    String word;
    String matchStatus;
    String nickname;
    boolean timeOut;

    public SubmitResponse(String word, String matchStatus, String nickname) {
        this.word = word;
        this.matchStatus = matchStatus;
        this.nickname = nickname;
    }
}
