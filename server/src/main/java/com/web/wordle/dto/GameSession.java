package com.web.wordle.dto;

import com.web.wordle.util.GameUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameSession {
    String answer;
    List<String> playerList = new ArrayList<>();
    String turn;
    boolean[] hint;
    Timer timer;

    public String changeTurn(){
        this.turn = playerList.indexOf(this.turn) == 0 ? playerList.get(1) : playerList.get(0);
        return this.turn;
    }

    public void firstTurn(){
        this.turn = playerList.get(GameUtil.generateRandomInt());
    }

}
