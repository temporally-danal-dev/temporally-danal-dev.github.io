package com.web.wordle.service;

import com.web.wordle.dto.MatchingResponse;
import com.web.wordle.dto.MatchingResponse.ResponseResult;
import com.web.wordle.util.GameUtil;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MatchingService {
    static Hashtable<String, DeferredResult<MatchingResponse>> pool = new Hashtable<>();

    @Async("asyncTreadPool")
    public void join(String userId,DeferredResult<MatchingResponse> deferredResult) {
        pool.put(userId, deferredResult);
        createRoom();
    }

    private void createRoom() {
        if(pool.size() < 2) return;

        Iterator<String> itr = pool.keySet().iterator();
        String user1 = itr.next();
        String user2 = itr.next();

        //String player1 = GameUtil.generateNickname();
        //String player2 = GameUtil.generateNickname();



        String uuid = UUID.randomUUID().toString();

        DeferredResult<MatchingResponse> user1Result = pool.remove(user1);
        DeferredResult<MatchingResponse> user2Result = pool.remove(user2);

        user1Result.setResult(new MatchingResponse(ResponseResult.SUCCESS,user1,user2,uuid));
        user2Result.setResult(new MatchingResponse(ResponseResult.SUCCESS, user2,user1,uuid));

    }

    public void cancelMatching(String sessionId) {
        DeferredResult<MatchingResponse> cancelUser = pool.remove(sessionId);
        if(cancelUser == null) return;
        cancelUser.setResult(new MatchingResponse(ResponseResult.CANCEL,"","",""));
    }

    public void timeOut(String sessionId) {
        DeferredResult<MatchingResponse> timeOutUser = pool.remove(sessionId);
        if(timeOutUser == null) return;
        timeOutUser.setResult(new MatchingResponse(ResponseResult.TIMEOUT,"","",""));
    }
}
