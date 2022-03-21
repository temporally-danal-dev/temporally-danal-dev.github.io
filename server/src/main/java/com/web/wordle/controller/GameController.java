package com.web.wordle.controller;

import com.web.wordle.dto.*;
import com.web.wordle.service.GameService;
import lombok.AllArgsConstructor;
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

    //join -> matching
    @MessageMapping("/{roomId}/join")
    public void join(@DestinationVariable String roomId, JoinRequest req){
        log.info("JOIN");
        GameSession gameSession = gameService.join(roomId,req);
        if(gameSession.getPlayerList().size()==2){
            StartResponse startResponse = gameService.start(roomId);
            template.convertAndSend("/sub/" + roomId+"/start", startResponse);;
        }
    }
    //submit  answer, nickname, turn(optional) => nickname, answer , ball count , turn(optional),next(optional)
    @MessageMapping("/{roomId}/submit")
    public void submit(@DestinationVariable String roomId, SubmitRequest submitRequest){

        ErrorResponse errorResponse = gameService.validationCheck(roomId,submitRequest);

        if(errorResponse != null){
            template.convertAndSend("/sub/" + submitRequest.getNickname()+"/error", errorResponse);
            return;
        }

        if(gameService.end(roomId,submitRequest.getWord())){//게임 속행
            log.info("SUBMIT");
            SubmitResponse submitResponse = gameService.submit(roomId,submitRequest);
            template.convertAndSend("/sub/" + roomId+"/submit", submitResponse);
        } else {// 게임 종료
            log.info("END");
            template.convertAndSend("/sub/" + roomId + "/end", new EndResponse(submitRequest.getNickname(), submitRequest.getWord()));
        }
    }

    @MessageMapping("/{roomId}/hint")
    public void hint(@DestinationVariable String roomId, HintRequest hintRequest){
        ErrorResponse errorResponse = gameService.hintCheck(roomId,hintRequest);

        if(errorResponse != null){
            template.convertAndSend("/sub/"+hintRequest.getNickname()+"/hint", errorResponse);
            return;
        }

        template.convertAndSend("/sub/" + roomId + "/hint", gameService.hint(roomId, hintRequest));
    }
}
