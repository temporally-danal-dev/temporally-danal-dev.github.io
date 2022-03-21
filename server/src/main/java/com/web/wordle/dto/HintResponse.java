package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HintResponse {
    String nickname;
    String word;
    String matchStatus;
}
