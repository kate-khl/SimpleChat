package org.khl.chat.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.khl.chat.entity.Message;
import org.khl.chat.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageDao extends JpaRepository<Message, Long> {
	
	List<Message> findMessageByChatId(Long id, Pageable page);

}
