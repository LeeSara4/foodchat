package chatBot.service;

import chatBot.dao.ChatBotDAO;

public class InsertService {
	ChatBotDAO dao = new ChatBotDAO();
	
	public void insert(String requestData) {
		dao.insertWord(requestData);
	}
}
