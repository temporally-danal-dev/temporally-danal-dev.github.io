package com.web.wordle.service;

import com.web.wordle.dao.GameDao;
import com.web.wordle.dto.*;
import com.web.wordle.entity.Word;
import com.web.wordle.util.GameUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class GameService {

    static Map<String, GameSession> gameList = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate template;

    private Map<String, String> connectedUsers;

    private final GameDao gameDao;

    @PostConstruct
    private void setUp(){
        connectedUsers = new ConcurrentHashMap<>();
    }

    public synchronized GameSession join(String roomId, JoinRequest req) {
        GameSession gameSession;
        if(gameList.containsKey(roomId)){
            gameSession = gameList.get(roomId);
            gameSession.getPlayerList().add(req.getNickname());

        } else {
            gameSession = new GameSession();
            gameSession.getPlayerList().add(req.getNickname());
            gameSession.setHint(new boolean[2]);

            List<Word> wordList = gameDao.findAll();
            Collections.shuffle(wordList);
            gameSession.setAnswer(wordList.get(0).getWord());
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

    public ErrorResponse validationCheck(String roomId, SubmitRequest submitRequest){
        if(GameUtil.validationTurnCheck(gameList.get(roomId),submitRequest.getNickname())){
            return new ErrorResponse(ErrorResponse.ErrorType.TURN,"플레이어의 턴이 아닙니다.");
        } else if (GameUtil.validationLengthCheck(gameList.get(roomId),submitRequest.getWord())){
            return new ErrorResponse(ErrorResponse.ErrorType.LENGTH,"입력값의 길이가 올바르지 않습니다.");
        } else if(GameUtil.validationValueCheck(submitRequest.getWord())){
            return new ErrorResponse(ErrorResponse.ErrorType.VALUE,"영어 소문자만 입력해주세요.");
        }
        return null;
    }

    public ErrorResponse hintCheck(String roomId, HintRequest hintRequest){
        if(GameUtil.validationTurnCheck(gameList.get(roomId),hintRequest.getNickname())){
            return new ErrorResponse(ErrorResponse.ErrorType.TURN,"플레이어의 턴이 아닙니다.");
        } else if(GameUtil.validationHintCheck(gameList.get(roomId),hintRequest.getNickname())){
            return new ErrorResponse(ErrorResponse.ErrorType.HINT,"이미 힌트를 사용했습니다.");
        }
        return null;
    }

    //게임이 끝났는지 체크
    public boolean end(String roomId, String word){
        return !gameList.get(roomId).getAnswer().equals(word);
    }

    public void connectUser(String roomId, String sessionId) {
        //세션 아이디로 룸아이디 저장
        connectedUsers.put(sessionId,roomId);
    }

    public void disconnectUser(String sessionId) {
        //세션에 있는 룸아이디로 전부 end 요청 보내주기.
        String roomId = connectedUsers.get(sessionId);
        if(roomId.equals(null) || gameList.get(roomId).equals(null)) return;
        template.convertAndSend("/sub/" + roomId+"/end", new EndResponse("",gameList.get(roomId).getAnswer()));
    }

    public HintResponse hint(String roomId, HintRequest hintRequest) {

        gameList.get(roomId).getHint()[gameList.get(roomId).getPlayerList().indexOf(hintRequest.getNickname())] = true;

        int location = hintRequest.getLocation();
        String answer = gameList.get(roomId).getAnswer();
        char hintChar = answer.charAt(location);

        StringBuilder word = new StringBuilder();
        StringBuilder matchStatus = new StringBuilder();

        for(int i = 0; i < answer.length(); i++){
            if(location == i) {
                word.append(hintChar);
                matchStatus.append("2");
            } else {
                word.append(" ");
                matchStatus.append("0");
            }
        }

        return new HintResponse(hintRequest.getNickname(), word.toString(), matchStatus.toString());
    }
}