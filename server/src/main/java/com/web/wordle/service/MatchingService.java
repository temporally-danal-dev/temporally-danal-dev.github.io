package com.web.wordle.service;

import com.web.wordle.dto.MatchingResponse;
import com.web.wordle.util.GameUtil;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class MatchingService {
    static Hashtable<String, DeferredResult<MatchingResponse>> pool = new Hashtable<>();

    private Map<String, String> connectedUsers;

    @PostConstruct
    private void setUp(){
        connectedUsers = new ConcurrentHashMap<>();
    }

    public void connectUser(String matchingRoomId, String sessionId) {
        connectedUsers.put(matchingRoomId,sessionId);
    }

    @Async("asyncTreadPool")
    public void join(String sessionId,DeferredResult<MatchingResponse> deferredResult) {
        pool.put(sessionId, deferredResult);
        createRoom();
    }

    private void createRoom() {
        if(pool.size() < 2) return;

        Iterator<String> itr = pool.keySet().iterator();
        String user1 = itr.next();
        String user2 = itr.next();

        String player1 = GameUtil.generateNickname();
        String player2 = GameUtil.generateNickname();

        String uuid = UUID.randomUUID().toString();

        DeferredResult<MatchingResponse> user1Result = pool.remove(user1);
        DeferredResult<MatchingResponse> user2Result = pool.remove(user2);

        user1Result.setResult(new MatchingResponse(player1,player2,uuid));
        user2Result.setResult(new MatchingResponse(player2,player1,uuid));

    }


}
