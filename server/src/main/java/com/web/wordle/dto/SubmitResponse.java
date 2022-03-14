package com.web.wordle.dto;

import lombok.Data;

@Data
public class SubmitResponse {
    String word;
    String matchStatus;
    String nickname;

}
