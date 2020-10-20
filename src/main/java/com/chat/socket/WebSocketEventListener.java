package com.chat.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class WebSocketEventListener {

    private Set<String> liveUsers = new HashSet<>();
    public Map<String, String> liveUsersMap = new HashMap<>();

    public Map<String, String> userListUpdateMap = new HashMap<>();
    public Map<String, String> currentChattingUsersListUpdateMap = new HashMap<>();
    public Map<String, String> messageListUpdateMap = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
        /*System.out.println("===> Connect");
        System.out.println(event);*/
        liveUsers.add(event.getMessage().getHeaders().get("simpSessionId").toString());
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        messagingTemplate.convertAndSend("/topic/allUser", liveUsers);
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        /*System.out.println("Disconnect <====");
        System.out.println(event);*/

        userListUpdateMap.remove(event.getSessionId());
        currentChattingUsersListUpdateMap.remove(event.getSessionId());
        messageListUpdateMap.remove(event.getSessionId());


        liveUsers.remove(event.getSessionId());
        liveUsersMap.remove(event.getSessionId());


        messagingTemplate.convertAndSend("/topic/allUser", liveUsers);
    }

    public Set<String> getLiveUsers() {
        return liveUsers;
    }

    public Map<String, String> getLiveUsersMap() {
        return liveUsersMap;
    }
}
