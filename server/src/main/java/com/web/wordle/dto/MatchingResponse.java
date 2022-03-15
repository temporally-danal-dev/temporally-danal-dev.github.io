package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchingResponse {
    String me;
    String opponent;
    String roomId;
}
