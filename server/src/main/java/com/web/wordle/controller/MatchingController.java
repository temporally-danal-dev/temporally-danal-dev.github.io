package com.web.wordle.controller;

import com.web.wordle.service.MatchingService;
import com.web.wordle.util.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@CrossOrigin
@RequestMapping("/matching")
public class MatchingController {

    @Autowired
    MatchingService matchingService;

    @GetMapping
    @ResponseBody
    public DeferredResult<String> join(){
        String sessionId = ServletUtil.getSession().getId();
        final DeferredResult<String> deferredResult = new DeferredResult<>(null);
        matchingService.join(sessionId,deferredResult);

        return deferredResult;
    }

}
