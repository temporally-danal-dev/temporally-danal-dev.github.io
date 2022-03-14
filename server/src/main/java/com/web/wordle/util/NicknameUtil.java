package com.web.wordle.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NicknameUtil {
    public static String generateNickname() {
        List<String> nick = Arrays.asList("기분나쁜","기분좋은","신바람나는","상쾌한","짜릿한","그리운","자유로운","서운한",
                "울적한","비참한","위축되는","긴장되는","두려운","당당한","배부른","수줍은","창피한","멋있는", "열받은","심심한","잘생긴","이쁜","시끄러운","활발한");
        List<String> name = Arrays.asList("사자","코끼리","호랑이","곰","여우","늑대","너구리","침팬치","고릴라","참새","엘든링",
                "고슴도치","강아지","고양이","거북이","토끼","앵무새","하이에나","돼지","하마","원숭이","물소","얼룩말","치타", "악어","기린","수달","염소","다람쥐","판다","드래곤");
        Collections.shuffle(nick);
        Collections.shuffle(name);
        String text = nick.get(0)+" "+name.get(0);
        return text;
    }
}
