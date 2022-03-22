package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmitRequest {
    String word;
    String nickname;
}
