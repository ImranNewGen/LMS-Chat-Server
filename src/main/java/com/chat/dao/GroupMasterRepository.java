package com.chat.dao;

import com.chat.domain.GroupMaster;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMasterRepository extends JpaRepository<GroupMaster, Long> {
    List<GroupMaster> findAllByNameAndMemberNot(String name,String memberNo);
    List<GroupMaster> findAllByName(String name);
    List<GroupMaster> findAllByMember(String member);

    void deleteAllByName(String name);
    void deleteAllByNameAndMember(String name, String member);
    boolean existsByName(String name);
}

