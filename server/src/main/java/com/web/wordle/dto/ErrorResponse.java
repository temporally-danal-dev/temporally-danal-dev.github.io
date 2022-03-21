package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private ErrorType errorType;
    private String errorMsg;

    public enum ErrorType {
        TURN, LENGTH, VALUE, HINT;
    }
}
