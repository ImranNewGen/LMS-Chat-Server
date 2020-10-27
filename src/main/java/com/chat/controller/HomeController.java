package com.chat.controller;

import com.chat.dao.ChatMessageRepository;
import com.chat.dao.GroupMasterRepository;
import com.chat.domain.ChatMessage;
import com.chat.domain.GroupMaster;
import com.chat.form.AddEditGroupForm;
import com.chat.form.Greeting;
import com.chat.form.GreetingMessage;
import com.chat.socket.WebSocketEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    private GroupMasterRepository groupMasterRepository;


    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/delete")
    public String delete(Model model) {
        chatMessageRepository.deleteAll();
        groupMasterRepository.deleteAll();
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



    @MessageMapping("/sendMessageEnter")
    public void sendMessageEnter(@Payload ChatMessage chatMessage) {
        List<GroupMaster> groupMasters = groupMasterRepository.findAllByNameAndMemberNot(chatMessage.getReceiver(), chatMessage.getSender());

        if (!groupMasters.isEmpty()) {
            String groupName = chatMessage.getReceiver();
            Set<String> members = groupMasters.stream().map(GroupMaster::getMember).collect(Collectors.toSet());
            chatMessage.setOwner(chatMessage.getSender());
            chatMessageRepository.save(chatMessage);

            for (String member : members) {
                chatMessage.setSender(groupName);
                chatMessage.setReceiver(member);
                chatMessage.setId(null);
                chatMessageRepository.save(chatMessage);

                List<String> sessions = webSocketEventListener.liveUsersMap.entrySet().stream().filter(f -> f.getValue().equals(member))
                        .map(Map.Entry::getKey).collect(Collectors.toList());

                for (String session : sessions) {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put("newMessage", chatMessage);
                    obj.put("userList", webSocketEventListener.userListUpdateMap.get(session));
                    obj.put("currentChattingUsersList", webSocketEventListener.currentChattingUsersListUpdateMap.get(session));
                    obj.put("messageList", webSocketEventListener.messageListUpdateMap.get(session));
                    messagingTemplate.convertAndSend("/topic/receive-" + session, obj);
                }
            }
        } else {

            chatMessageRepository.save(chatMessage);

            List<String> sessions = webSocketEventListener.liveUsersMap.entrySet().stream().filter(f -> f.getValue().equals(chatMessage.getReceiver()))
                    .map(Map.Entry::getKey).collect(Collectors.toList());

            for (String session : sessions) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("newMessage", chatMessage);
                obj.put("userList", webSocketEventListener.userListUpdateMap.get(session));
                obj.put("currentChattingUsersList", webSocketEventListener.currentChattingUsersListUpdateMap.get(session));
                obj.put("messageList", webSocketEventListener.messageListUpdateMap.get(session));
                messagingTemplate.convertAndSend("/topic/receive-" + session, obj);
            }
        }
    }


    @Transactional
    @MessageMapping("/addGroup")
    public void addGroup(@Payload AddEditGroupForm form, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getHeader("simpSessionId").toString();
        if (groupMasterRepository.existsByName(form.getNewGname())) {
            messagingTemplate.convertAndSend("/topic/messageConfirmation-" + sessionId, "Group Name Already Exists");
        } else {
            List<GroupMaster> masters = form.getDetails().stream().map(m -> new GroupMaster(form.getNewGname(), m)).collect(Collectors.toList());
            groupMasterRepository.saveAll(masters);
            groupMasterRepository.save(new GroupMaster(form.getNewGname(), webSocketEventListener.liveUsersMap.get(sessionId)));
            messagingTemplate.convertAndSend("/topic/messageConfirmation-" + sessionId, "Created Successfully");
            messagingTemplate.convertAndSend("/topic/userListUpdateAll", "");
        }

    }

    @Transactional
    @MessageMapping("/editGroup")
    public void editGroup(@Payload AddEditGroupForm form, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getHeader("simpSessionId").toString();
        if (!form.getNewGname().equals(form.getOldGname()) && groupMasterRepository.existsByName(form.getNewGname())) {
            messagingTemplate.convertAndSend("/topic/messageConfirmation-" + sessionId, "Group Name Already Exists");
        } else {
            groupMasterRepository.deleteAllByName(form.getOldGname());
            List<GroupMaster> masters = form.getDetails().stream().map(m -> new GroupMaster(form.getNewGname(), m)).collect(Collectors.toList());
            groupMasterRepository.saveAll(masters);
            groupMasterRepository.save(new GroupMaster(form.getNewGname(), webSocketEventListener.liveUsersMap.get(sessionId)));
            if (!form.getNewGname().equals(form.getOldGname())) {
                chatMessageRepository.updateChatSender(form.getOldGname(), form.getNewGname());
                chatMessageRepository.updateChatReceiver(form.getOldGname(), form.getNewGname());
            }
            messagingTemplate.convertAndSend("/topic/messageConfirmation-" + sessionId, "Updated Successfully");
            messagingTemplate.convertAndSend("/topic/userListUpdateAll", "");
        }
    }

    @Transactional
    @MessageMapping("/leaveGroup")
    public void leaveGroup(@Payload String gname, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getHeader("simpSessionId").toString();
        String username = webSocketEventListener.liveUsersMap.get(sessionId);
        groupMasterRepository.deleteAllByNameAndMember(gname, username);
        messagingTemplate.convertAndSend("/topic/messageConfirmation-" + sessionId, "Left Successfully");
        messagingTemplate.convertAndSend("/topic/userListUpdateAll", "");
    }
}

