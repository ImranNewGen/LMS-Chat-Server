package com.chat.controller;

import com.chat.dao.ChatMessageRepository;
import com.chat.dao.GroupMasterRepository;
import com.chat.domain.ChatMessage;
import com.chat.domain.GroupMaster;
import com.chat.form.AddEditGroupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class HomeRestController {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    GroupMasterRepository groupMasterRepository;


    @GetMapping("/allChatMessage/{sender}")
    public ResponseEntity<?> allChatMessage(@PathVariable("sender") String sender) {
        return ResponseEntity.ok(chatMessageRepository.findAllBySenderOrReceiver(sender, sender));
    }

    @GetMapping("/myGroupList/{username}")
    public ResponseEntity<?> myGroupList(@PathVariable("username") String username) {
        List<Map<String, Object>> collect = groupMasterRepository.findAllByMember(username)
                .stream().map(GroupMaster::getName)
                .collect(Collectors.toSet()).stream().map(m -> {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put("id", "");
                    obj.put("username", m);
                    obj.put("fullName", m);
                    obj.put("image", "data:image/png;base64,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");
                    obj.put("group", true);
                    return obj;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }

    @GetMapping("/getAllMembers/{gname}")
    public ResponseEntity<?> getAllMembers(@PathVariable("gname") String gname) {
        return ResponseEntity.ok(groupMasterRepository.findAllByName(gname).stream().map(GroupMaster::getMember).collect(Collectors.toSet()));
    }

    @GetMapping("/dummyUser")
    public ResponseEntity<?> dummyUser() {

        Map<String, Object> obj1 = new HashMap<>();
        obj1.put("id", 1);
        obj1.put("username", "imran");
        obj1.put("fullName", "M Imran");
        obj1.put("image", "dist/img/avatar.png");

        Map<String, Object> obj2 = new HashMap<>();
        obj2.put("id", 2);
        obj2.put("username", "shitul");
        obj2.put("fullName", "Shitul R");
        obj2.put("image", "dist/img/avatar2.png");

        Map<String, Object> obj3 = new HashMap<>();
        obj3.put("id", 3);
        obj3.put("username", "kamal");
        obj3.put("fullName", "Kamal");
        obj3.put("image", "dist/img/avatar3.png");

        Map<String, Object> obj4 = new HashMap<>();
        obj4.put("id", 4);
        obj4.put("username", "jamal");
        obj4.put("fullName", "Jamal");
        obj4.put("image", "dist/img/avatar5.png"); 
        
        Map<String, Object> obj5 = new HashMap<>();
        obj5.put("id", 5);
        obj5.put("username", "salam");
        obj5.put("fullName", "Salam");
        obj5.put("image", "dist/img/user1-128x128.jpg");

        return ResponseEntity.ok(new ArrayList<>(Arrays.asList(obj1, obj2, obj3, obj4, obj5)));
    }

}