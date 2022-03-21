package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HintRequest {
    String nickname;
    int location;
}
