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
        if(gameService.join(roomId,req).getPlayerList().size()==2){
            log.info("START");
            gameService.start(roomId);
            //template.convertAndSend("/sub/" + roomId+"/start", gameService.start(roomId));;
        }
    }
    //submit  answer, nickname, turn(optional) => nickname, answer , ball count , turn(optional),next(optional)
    @MessageMapping("/{roomId}/submit")
    public void submit(@DestinationVariable String roomId, SubmitRequest submitRequest){

        //ErrorResponse errorResponse = gameService.validationCheck(roomId,submitRequest);

        if(gameService.validationCheck(roomId,submitRequest)){
            //template.convertAndSend("/sub/" + submitRequest.getNickname()+"/error", errorResponse);
            return;
        }

        if(gameService.checkEnd(roomId,submitRequest.getWord())){//게임 속행
            log.info("SUBMIT");
            gameService.submit(roomId,submitRequest, false);
        } else {// 게임 종료
            log.info("END");
            gameService.end(roomId, submitRequest.getNickname(), submitRequest.getWord());
            //gameService.messageSend(gameService.generateAddress(roomId,"end"),new EndResponse(submitRequest.getNickname(), submitRequest.getWord()));
            //template.convertAndSend("/sub/" + roomId + "/end", new EndResponse(submitRequest.getNickname(), submitRequest.getWord()));
        }
    }

    @MessageMapping("/{roomId}/hint")
    public void hint(@DestinationVariable String roomId, HintRequest hintRequest){
        //ErrorResponse errorResponse = gameService.hintCheck(roomId,hintRequest);

        if(gameService.hintCheck(roomId,hintRequest)){
            //template.convertAndSend("/sub/"+hintRequest.getNickname()+"/hint", errorResponse);
            return;
        }

        gameService.hint(roomId, hintRequest);
        //template.convertAndSend("/sub/" + roomId + "/hint", gameService.hint(roomId, hintRequest));
    }
}
