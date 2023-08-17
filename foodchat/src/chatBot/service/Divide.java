package chatBot.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import chatBot.dao.ChatBotDAO;
import util.DBUtil;

public class Divide {
	ChatBotDAO dao = new ChatBotDAO();

	public void divide(Connection conn, List<String> chat
			, List<String> words, List<String> foods) {
		for (String string : chat) {
			boolean flag = dao.searchFood(conn, string);
			if (flag) {
				foods.add(string);
			} else {
				words.add(string);
			}
		}
	}

}
