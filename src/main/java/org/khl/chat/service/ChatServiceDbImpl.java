package org.khl.chat.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.khl.chat.Session;
import org.khl.chat.dao.ChatDao;
import org.khl.chat.dao.MessageDao;
import org.khl.chat.dao.UserDao;
import org.khl.chat.entity.Chat;
import org.khl.chat.entity.Message;
import org.khl.chat.entity.User;
import org.khl.chat.exception.AccessControlException;
import org.khl.chat.model.ChatDto;
import org.khl.chat.model.CreateRequestChat;
import org.khl.chat.model.MessageDto;
import org.khl.chat.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import antlr.collections.List;

@Service
@Qualifier("db")
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST)
public class ChatServiceDbImpl implements ChatService{

	@Autowired
	private ChatDao chDao;
	@Autowired
	private UserDao uDao;
	@Autowired
	private MessageDao msgDao;
	@Autowired
	private Session session;
	
	@Override
	public ChatDto createChat(CreateRequestChat crChat) {
		
		Chat c = new Chat();
		c.setName(crChat.getName());
		c.setUsers(uDao.findAllById(crChat.getUsers()));
		
		User author = uDao.findById(session.getId()).get();
		c.getUsers().add(author);
		c.setAuthor(author);
		chDao.save(c);
		
		Message msg = new Message(crChat.getMessage(), author, c);
		msgDao.save(msg);
		
		Collection<Message> msgList = new ArrayList<Message>();
		msgList.add(msg);
		c.setMessages(msgList);
		
		return new ChatDto(chDao.save(c));
	}

	@Override
	public ChatDto findChat(Long id) {
		Chat chat = chDao.getOne(id);
		return new ChatDto(chat);
	}

	@Override
	public void addUsers(Collection<Long> userIds, Long chatId) {
		Collection<User> users = uDao.findAllById(userIds);
		Chat chat = chDao.getOne(chatId);
		chat.getUsers().addAll(users);
		chDao.save(chat);
	}

	@Override
	public void removeUsers(Collection<Long> userIds, Long chatId) {
		
		Collection<User> users = uDao.findAllById(userIds);
		Chat chat = chDao.getOne(chatId);
		chat.getUsers().removeAll(users);
		chDao.save(chat);
		
	}

	@Override
	public Collection<UserDto> getUsers(Long chatId) {
		Chat c = chDao.getOne(chatId);
		ChatDto chatDao = new ChatDto(c);
		
		return chatDao.getUsers();
	}

	@Override
	public void removeChat(Long id) {
		Chat c = chDao.getOne(id);
		if(c.getAuthor().getId().equals(session.getId())) {
			chDao.delete(c);
		} else 
		throw new AccessControlException();
		
	}
	
	@Override
	public Collection<MessageDto> getMessages(Long chatId){
		Collection<MessageDto> msgs = chDao.findById(chatId).get().getMessagesDto();
		return msgs;
	}
	

}
