package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmitResponse {
    String word;
    String matchStatus;
    String nickname;
    boolean timeOut;
}
