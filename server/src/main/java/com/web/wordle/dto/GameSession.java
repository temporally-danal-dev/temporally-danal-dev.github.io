package com.web.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameSession {
    String answer;
    List<String> playerList = new ArrayList<>();
    String turn;
    boolean[] hint;

    public String changeTurn(){
        return this.turn = playerList.indexOf(this.turn) == 0 ? playerList.get(1) : playerList.get(0);
    }

    public void firstTurn(){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        this.turn = playerList.get(random.nextInt(1));
    }

}
