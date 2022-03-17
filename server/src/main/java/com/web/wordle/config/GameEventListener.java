package com.web.wordle.config;

import com.web.wordle.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@AllArgsConstructor
@Slf4j
public class GameEventListener {

    private GameService gameService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        GenericMessage<?> generic = (GenericMessage<?>) accessor.getHeader("simpConnectMessage");
        Map<String, Object> nativeHeaders = (Map<String, Object>) generic.getHeaders().get("nativeHeaders");

        String matchingRoomId;
        if(nativeHeaders.containsKey("roomId")) {
            matchingRoomId = ((List<String>) nativeHeaders.get("roomId")).get(0);
        } else {
            return;
        }

        String sessionId = (String) generic.getHeaders().get("simpSessionId");
        log.info("[Connected] room id : {} | websocket session id : {}", matchingRoomId, sessionId);

        gameService.connectUser(matchingRoomId, sessionId);//여기서 게임 종류 인원에 맞는 작업을 가져와야함
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();

        log.info("[Disconnected] websocket session id : {}", sessionId);
        gameService.disconnectUser(sessionId);
    }
}
