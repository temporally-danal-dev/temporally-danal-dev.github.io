package com.web.wordle.controller;

import com.web.wordle.dto.MatchingResponse;
import com.web.wordle.service.MatchingService;
import com.web.wordle.util.ServletUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/matching")
public class MatchingController {

    private MatchingService matchingService;

    @GetMapping
    @ResponseBody
    public DeferredResult<MatchingResponse> join(){
        System.out.println("hi");
        String sessionId = ServletUtil.getSession().getId();
        final DeferredResult<MatchingResponse> deferredResult = new DeferredResult<>(null);
        matchingService.join(sessionId,deferredResult);
        return deferredResult;
    }

}
