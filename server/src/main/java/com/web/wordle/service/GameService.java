package com.web.wordle.service;

import com.web.wordle.dto.*;
import com.web.wordle.util.GameUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class GameService {

    static Map<String, GameSession> gameList = new ConcurrentHashMap<>();

    public synchronized GameSession join(String roomId, JoinRequest req) {
        GameSession gameSession;
        if(gameList.containsKey(roomId)){
            gameSession = gameList.get(roomId);
            gameSession.getPlayerList().add(req.getNickname());

        } else {
            gameSession = new GameSession();
            gameSession.getPlayerList().add(req.getNickname());

            gameSession.setAnswer(GameUtil.generateAnswer());
        }
        gameList.put(roomId,gameSession);
        return gameSession;
    }

    public StartResponse start(String roomId) {
        gameList.get(roomId).firstTurn();
        GameSession gameSession = gameList.get(roomId);
        return new StartResponse(gameSession.getTurn(),gameSession.getAnswer().length());
    }

    public SubmitResponse submit(String roomId, SubmitRequest submitRequest) {
        String input = submitRequest.getWord();
        String answer = gameList.get(roomId).getAnswer();
        String output = "";

        for(int i = 0; i < answer.length(); i++){
            if(input.charAt(i) == answer.charAt(i)){//전부 일치
                output +="2";
            } else if(answer.contains(input.substring(i,i+1))){//글자만 일치
                output +="1";
            } else {
                output +="0";
            }

        }

        return new SubmitResponse(input, output, gameList.get(roomId).changeTurn());
    }

    //validation check 지금 submit 한 애가 제대로 된 턴인지 체크
    public boolean validationCheck(String roomId,String nickname){
        return !gameList.get(roomId).getTurn().equals(nickname);
    }

    //게임이 끝났는지 체크
    public boolean end(String roomId, String word){
        return !gameList.get(roomId).getAnswer().equals(word);
    }
}
