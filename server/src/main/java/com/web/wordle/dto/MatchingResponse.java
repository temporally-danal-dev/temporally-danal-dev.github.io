package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchingResponse {
    private ResponseResult responseResult;
    private String me;
    private String opponent;
    private String roomId;

    public enum ResponseResult {
        SUCCESS, CANCEL, TIMEOUT;
    }
}
