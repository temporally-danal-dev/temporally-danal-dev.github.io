package com.web.wordle.controller;

import com.web.wordle.dto.EndResponse;
import com.web.wordle.dto.JoinRequest;
import com.web.wordle.dto.StartResponse;
import com.web.wordle.dto.SubmitResponse;
import com.web.wordle.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final SimpMessagingTemplate template;
    private GameService gameService;

    //join -> matching
    @MessageMapping("/{roomId}/join")
    public void join(@DestinationVariable String roomId, JoinRequest req){
        //2명되면 start
        //data에 put

        //2명되면 start
    }
    //start - nickname 보내서 누가 선공인지, 단어 길이 보내서 몇글자짜리 인지, turn 정보
    @MessageMapping("/{roomId}/start")
    public void start(@DestinationVariable String roomId){
        template.convertAndSend("/sub/" + roomId+"/start", new StartResponse());;
    }

    //submit  answer, nickname, turn(optional) => nickname, answer , ball count , turn(optional),next(optional)
    @MessageMapping("/{roomId}/submit")
    public void submit(@DestinationVariable String roomId){
        if(true){//게임 속행
            template.convertAndSend("/sub/" + roomId+"/submit", new SubmitResponse());;
        } else {// 게임 종료
            template.convertAndSend("/sub/" + roomId+"/end", new EndResponse());;
        }
    }
}
