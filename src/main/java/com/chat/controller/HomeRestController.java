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
                    obj.put("image", "dist/img/boxed-bg.jpg");
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