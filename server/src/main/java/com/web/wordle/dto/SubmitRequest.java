package com.web.wordle.dto;

import lombok.Data;

@Data
public class SubmitRequest {
    String word;
    String nickname;
}
