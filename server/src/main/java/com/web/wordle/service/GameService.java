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
import java.util.*;
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
            gameSession.setTimer(new Timer());

            List<Word> wordList = gameDao.findAll();
            Collections.shuffle(wordList);
            gameSession.setAnswer(wordList.get(0).getWord());
        }

        gameList.put(roomId,gameSession);
        return gameSession;
    }

    public void start(String roomId) {
        gameList.get(roomId).firstTurn();
        GameSession gameSession = gameList.get(roomId);
        setGameSessionTimerTask(roomId);
        messageSend(generateAddress(roomId,"start"),new StartResponse(gameSession.getTurn(),gameSession.getAnswer().length()));
        //return new StartResponse(gameSession.getTurn(),gameSession.getAnswer().length());
    }

    public void submit(String roomId, SubmitRequest submitRequest, boolean timeOut) {
        String input = submitRequest.getWord();
        String answer = gameList.get(roomId).getAnswer();
        StringBuilder output = new StringBuilder();

        setGameSessionTimerTask(roomId);
        for(int i = 0; i < answer.length(); i++){
            if(input.charAt(i) == answer.charAt(i)){//전부 일치
                output.append("2");
            } else if(answer.contains(input.substring(i,i+1))){//글자만 일치
                output.append("1");
            } else {
                output.append("0");
            }
        }

        //새 알고리즘

        messageSend(generateAddress(roomId,"submit"),new SubmitResponse(input, output.toString(), gameList.get(roomId).changeTurn(),timeOut));
        //return new SubmitResponse(input, output.toString(), gameList.get(roomId).changeTurn());
    }

    public void end(String roomId, String nickname, String word) {
        messageSend(generateAddress(roomId, "end"),new EndResponse(nickname,word));
    }

    public void hint(String roomId, HintRequest hintRequest) {

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

        messageSend(generateAddress(roomId,"hint"),new HintResponse(hintRequest.getNickname(), word.toString(), matchStatus.toString()));

        //return new HintResponse(hintRequest.getNickname(), word.toString(), matchStatus.toString());
    }

    public void setGameSessionTimerTask(String roomId){

        gameList.get(roomId).getTimer().cancel();
        gameList.get(roomId).setTimer(new Timer());

        String generatedString = GameUtil.generateRandomString(gameList.get(roomId).getAnswer().length());

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //SubmitResponse submitResponse = submit(roomId,new SubmitRequest(generatedString,gameList.get(roomId).getTurn()));
                submit(roomId,new SubmitRequest(generatedString,gameList.get(roomId).getTurn()),true);
                //template.convertAndSend("/sub/"+roomId+"/submit", submitResponse);
            }
        };

        gameList.get(roomId).getTimer().schedule(task,90000);
    }

    public boolean validationCheck(String roomId, SubmitRequest submitRequest){
        if(GameUtil.validationTurnCheck(gameList.get(roomId),submitRequest.getNickname())){
            messageSend(generateAddress(submitRequest.getNickname(),"error"),new ErrorResponse(ErrorResponse.ErrorType.TURN,"플레이어의 턴이 아닙니다."));
            //return new ErrorResponse(ErrorResponse.ErrorType.TURN,"플레이어의 턴이 아닙니다.");
            return true;
        } else if (GameUtil.validationLengthCheck(gameList.get(roomId),submitRequest.getWord())){
            messageSend(generateAddress(submitRequest.getNickname(),"error"),new ErrorResponse(ErrorResponse.ErrorType.LENGTH,"입력값의 길이가 올바르지 않습니다."));
            //return new ErrorResponse(ErrorResponse.ErrorType.LENGTH,"입력값의 길이가 올바르지 않습니다.");
            return true;
        } else if(GameUtil.validationValueCheck(submitRequest.getWord())){
            messageSend(generateAddress(submitRequest.getNickname(),"error"),new ErrorResponse(ErrorResponse.ErrorType.VALUE,"영어 소문자만 입력해주세요."));
            //return new ErrorResponse(ErrorResponse.ErrorType.VALUE,"영어 소문자만 입력해주세요.");
            return true;
        }
        return false;
    }

    public boolean hintCheck(String roomId, HintRequest hintRequest){
        if(GameUtil.validationTurnCheck(gameList.get(roomId),hintRequest.getNickname())){
            messageSend(generateAddress(hintRequest.getNickname(),"error"),new ErrorResponse(ErrorResponse.ErrorType.TURN,"플레이어의 턴이 아닙니다."));
            return true;
            //return new ErrorResponse(ErrorResponse.ErrorType.TURN,"플레이어의 턴이 아닙니다.");
        } else if(GameUtil.validationHintCheck(gameList.get(roomId),hintRequest.getNickname())){
            messageSend(generateAddress(hintRequest.getNickname(),"error"),new ErrorResponse(ErrorResponse.ErrorType.HINT,"이미 힌트를 사용했습니다."));
            return true;
            //return new ErrorResponse(ErrorResponse.ErrorType.HINT,"이미 힌트를 사용했습니다.");
        }
        return false;
    }

    //게임이 끝났는지 체크
    public boolean checkEnd(String roomId, String word){
        return !gameList.get(roomId).getAnswer().equals(word);
    }

    public void connectUser(String roomId, String sessionId) {
        //세션 아이디로 룸아이디 저장
        connectedUsers.put(sessionId,roomId);
    }

    public void disconnectUser(String sessionId) {
        //세션에 있는 룸아이디로 전부 end 요청 보내주기.
        String roomId = connectedUsers.get(sessionId);
        if(gameList.get(roomId) == null) return;
        messageSend(generateAddress(roomId,"end"),new EndResponse("",gameList.get(roomId).getAnswer()));
        //template.convertAndSend("/sub/" + roomId+"/end", new EndResponse("",gameList.get(roomId).getAnswer()));
    }

    public String generateAddress(String dest,String act){
        return new StringBuilder().append("/sub/").append(dest).append("/").append(act).toString();
    }

    public void messageSend(String address, Object o){
        template.convertAndSend(address, o);
    }
}