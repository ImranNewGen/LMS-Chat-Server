package com.chat.controller;

import com.chat.dao.ChatMessageRepository;
import com.chat.domain.ChatMessage;
import com.chat.form.Greeting;
import com.chat.form.GreetingMessage;
import com.chat.socket.WebSocketEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    @Autowired
    ChatMessageRepository chatMessageRepository;


    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/cu")
    public String cu(Model model) {
        model.addAttribute("users", webSocketEventListener.getLiveUsers());
        model.addAttribute("usersMap", webSocketEventListener.getLiveUsersMap());
        model.addAttribute("currentChattingUsersListUpdateMap", webSocketEventListener.currentChattingUsersListUpdateMap);
        return "cu";
    }

    @MessageMapping("/hello")
    public void greeting(@Payload Greeting message) {
        message.getReceiver().forEach(session -> messagingTemplate.convertAndSend("/topic/me-" + session, new GreetingMessage(message.getContent())));
    }

    /*start*/

    @MessageMapping("/welcome")
    public void welcome(@Payload String username, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getHeader("simpSessionId").toString();
        webSocketEventListener.liveUsersMap.put(sessionId, username);
    }

    @MessageMapping("/sendMessageEnter")
    public void sendMessageEnter(@Payload ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);

        List<String> sessions = webSocketEventListener.liveUsersMap.entrySet()
                .stream().filter(f -> f.getValue().equals(chatMessage.getReceiver()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (String session : sessions) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("newMessage", chatMessage);
            obj.put("userList", webSocketEventListener.userListUpdateMap.get(session));
            obj.put("currentChattingUsersList", webSocketEventListener.currentChattingUsersListUpdateMap.get(session));
            obj.put("messageList", webSocketEventListener.messageListUpdateMap.get(session));
            messagingTemplate.convertAndSend("/topic/receive-" + session, obj);
        }
    }

    @MessageMapping("/userListUpdate")
    public void userListUpdate(@Payload String value, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getHeader("simpSessionId").toString();
        webSocketEventListener.userListUpdateMap.put(sessionId, value);
    }

    @MessageMapping("/currentChattingUsersListUpdate")
    public void currentChattingUsersListUpdate(@Payload String value, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getHeader("simpSessionId").toString();
        webSocketEventListener.currentChattingUsersListUpdateMap.put(sessionId, value);
    }

    @MessageMapping("/messageListUpdate")
    public void messageListUpdate(@Payload String value, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getHeader("simpSessionId").toString();
        webSocketEventListener.messageListUpdateMap.put(sessionId, value);
    }

}