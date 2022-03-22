package com.web.wordle.util;

import com.web.wordle.dto.GameSession;

import java.util.Random;

public class GameUtil {

    public static String generateRandomString(int length){

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

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
