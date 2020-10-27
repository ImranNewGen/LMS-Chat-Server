package com.chat.dao;

import com.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllBySenderOrReceiver(String s, String r);

    @Modifying
    @Query("update ChatMessage c set c.sender=:newGname where c.sender=:oldGname")
    void updateChatSender(@Param("oldGname") String oldGname, @Param("newGname") String newGname);

    @Modifying
    @Query("update ChatMessage c set c.receiver=:newGname where c.receiver=:oldGname")
    void updateChatReceiver(@Param("oldGname") String oldGname, @Param("newGname") String newGname);
}
