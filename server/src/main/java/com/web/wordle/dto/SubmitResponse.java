package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class SubmitResponse {
    String word;
    String matchStatus;
    String nickname;
    boolean timeOut;
}
