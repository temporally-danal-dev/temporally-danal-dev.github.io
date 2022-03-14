package com.web.wordle.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;

@Service
public class MatchingService {
    static Hashtable<String, DeferredResult<String>> pool = new Hashtable<>();

    @Async("asyncTreadPool")
    public void join(String sessionId,DeferredResult<String> deferredResult) {
        pool.put(sessionId, deferredResult);
        createRoom();
    }

    private void createRoom() {
        if(pool.size() < 2) return;

        Iterator<String> itr = pool.keySet().iterator();
        String user1 = itr.next();
        String user2 = itr.next();

        String uuid = UUID.randomUUID().toString();

        DeferredResult<String> user1Result = pool.remove(user1);
        DeferredResult<String> user2Result = pool.remove(user2);

        user1Result.setResult(uuid);
        user2Result.setResult(uuid);

    }
}
