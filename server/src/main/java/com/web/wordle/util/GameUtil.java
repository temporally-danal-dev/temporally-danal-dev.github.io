package com.web.wordle.util;

import com.web.wordle.dto.GameSession;

public class GameUtil {

    public static boolean validationTurnCheck(GameSession gameSession, String nickname){
        return !gameSession.getTurn().equals(nickname);
    }

    public static boolean validationLengthCheck(GameSession gameSession,String word){
        return !(gameSession.getAnswer().length() == word.length());
    }

    public static boolean validationValueCheck(String word){
        return !word.matches("^[a-z]*$");
    }

    public static boolean validationHintCheck(GameSession gameSession, String nickname){
        return gameSession.getHint()[gameSession.getPlayerList().indexOf(nickname)];
    }

}
