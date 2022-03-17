package com.web.wordle.controller;

import com.web.wordle.dto.MatchingResponse;
import com.web.wordle.service.MatchingService;
import com.web.wordle.util.ServletUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
@Slf4j
public class MatchingController {

    private MatchingService matchingService;

    @GetMapping("/matching/{userId}")
    public DeferredResult<MatchingResponse> join(@PathVariable String userId){
        log.info("JOINING MATCHING userID : {}" , userId);
        final DeferredResult<MatchingResponse> deferredResult = new DeferredResult<>(null);
        matchingService.join(userId,deferredResult);

        deferredResult.onCompletion(() -> matchingService.cancelMatching(userId));
        deferredResult.onError((throwable) -> matchingService.cancelMatching(userId));
        deferredResult.onTimeout(() -> matchingService.timeOut(userId));

        return deferredResult;
    }

    @GetMapping("/delete/{userId}")
    public void cancel(@PathVariable String userId){
        log.info("DELETE MATCHING userID : {}" , userId);
        matchingService.cancelMatching(userId);
    }

}
