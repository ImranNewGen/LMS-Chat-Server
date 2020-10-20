package com.chat.controller;

import com.chat.dao.ChatMessageRepository;
import com.chat.domain.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class HomeRestController {

    @Autowired
    ChatMessageRepository chatMessageRepository;


    @GetMapping("/allChatMessage/{sender}")
    public ResponseEntity<?> allChatMessage(@PathVariable("sender") String sender) {
        return ResponseEntity.ok(chatMessageRepository.findAllBySenderOrReceiver(sender, sender));
    }

    @PostMapping("/sendMessageEnter")
    public ResponseEntity<?> sendMessageEnter(@RequestBody ChatMessage chatMessage){
        return ResponseEntity.ok(chatMessageRepository.save(chatMessage));
    }

}