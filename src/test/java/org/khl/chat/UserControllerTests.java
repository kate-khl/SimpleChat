package org.khl.chat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.khl.chat.dto.ChatDto;
import org.khl.chat.dto.CreateRequestChat;
import org.khl.chat.dto.LoginRequestDto;
import org.khl.chat.dto.RegistrationUserRequest;
import org.khl.chat.dto.UserDto;
import org.khl.chat.service.ChatService;
import org.khl.chat.service.TokenService;
import org.khl.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts="classpath:data.sql")
public class UserControllerTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MockMvc mokMvcWithoutFilters;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserService uService;
    @Autowired
    private TokenService tokenService;
//    @Autowired
//    private ChatService chatService;
    
    @BeforeEach
    public void setup() {
    	this.mokMvcWithoutFilters = MockMvcBuilders.webAppContextSetup(context).build();

	}
    
	@Test
	public void createUser() throws Exception {
    	mokMvcWithoutFilters.perform(post("/registration")	
				.content("{\r\n\"id\":null,\"name\":\"Тест\",\"email\":\"user0@test.com\",\"password\":\"123\",\"role\":\"user\"\r\n}")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}
	
	@Test
	public void authorization() throws Exception {
		mokMvcWithoutFilters.perform(post("/auth")	
				.content("{\"email\" : \"user4@test.com\",\"password\" : \"123\"}")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	public void removeUser() throws Exception {
		mockMvc.perform(delete("/users/{id}", 1000)
			.header("Authorization", tokenService.getToken("user2@test.com", "123")))
		.andExpect(status().isOk());
	}
	
	@Test
	public void readAllUsers() throws Exception {
		mockMvc.perform(get("/users/list")
			.header("Authorization", tokenService.getToken("user2@test.com", "123"))
	        .param("page", "1")
	        .param("size", "5") )   
		.andExpect(status().isOk());
	}
	
	@Test
	public void findUserById() throws Exception {
		mockMvc.perform(get("/users/{id}", 1001)
			.header("Authorization", tokenService.getToken("user2@test.com", "123")))
		.andExpect(status().isOk());
	}
	
	@Test
	public void editUser() throws Exception {

		mockMvc.perform(patch("/users/{id}", 1002)
			.header("Authorization", tokenService.getToken("user2@test.com", "123"))
			.content("{\"id\" : \"1002\",\"name\" : \"Петя\",\"email\" : \"user3@test.com\",\"role\" : \"user\"}")
			.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
//	@Test
//	public void getUsersfromChat() throws Exception {
//		
//		UserDto uDto1 = uService.create(new RegistrationUserRequest("Тест1", "getUsersFromChat1@test.ru", "123", "user"));
//		UserDto uDto2 = uService.create(new RegistrationUserRequest("Тест2", "getUsersFromChat1@test.ru", "123", "user"));
//		
//		Collection<Long> userIds = new ArrayList();
//		userIds.add(uDto1.getId());
//		userIds.add(uDto2.getId());
//
//		ChatDto chatDto = chatService.createChat(new CreateRequestChat(userIds, "Chat1", "Hello"));
//		
//		mockMvc.perform(get("/users/chats/{id}", chatDto.getId()))
//		.andExpect(status().isOk());
//	}
	
}
