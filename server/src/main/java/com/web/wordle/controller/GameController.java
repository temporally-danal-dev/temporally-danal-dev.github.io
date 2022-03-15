package com.web.wordle.controller;

import com.web.wordle.dto.*;
import com.web.wordle.service.GameService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin("*")
@AllArgsConstructor
@Slf4j
public class GameController {

    private final SimpMessagingTemplate template;

    private GameService gameService;

    //세션에 담겨야할 것
    //플레이어, 정답,

    //join -> matching
    @MessageMapping("/{roomId}/join")
    public void join(@DestinationVariable String roomId, JoinRequest req){
        log.info("Join");
        GameSession gameSession = gameService.join(roomId,req);
        if(gameSession.getPlayerList().size()==2){
            StartResponse startResponse = gameService.start(roomId);
            template.convertAndSend("/sub/" + roomId+"/start", startResponse);;
        }
    }
    //start - nickname 보내서 누가 선공인지, 단어 길이 보내서 몇글자짜리 인지, turn 정보
//    @MessageMapping("/{roomId}/start")
//    public void start(@DestinationVariable String roomId){
//        template.convertAndSend("/sub/" + roomId+"/start", new StartResponse());;
//    }

    //submit  answer, nickname, turn(optional) => nickname, answer , ball count , turn(optional),next(optional)
    @MessageMapping("/{roomId}/submit")
    public void submit(@DestinationVariable String roomId, SubmitRequest submitRequest){
        if(!gameService.end(roomId,submitRequest.getWord())){//게임 속행
            log.info("SUBMIT");
            SubmitResponse submitResponse = gameService.submit(roomId,submitRequest);
            template.convertAndSend("/sub/" + roomId+"/submit", submitResponse);;
        } else {// 게임 종료
            log.info("END");
            template.convertAndSend("/sub/" + roomId+"/end", new EndResponse(submitRequest.getNickname(),submitRequest.getWord()));;
        }
    }
}
