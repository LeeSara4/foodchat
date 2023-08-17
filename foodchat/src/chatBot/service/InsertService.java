package chatBot.service;

import java.sql.Connection;
import java.util.List;

import chatBot.dao.ChatBotDAO;
import chatBot.model.WordCategory;

public class InsertService {
	ChatBotDAO dao = new ChatBotDAO();

	public int insert(Connection conn, String word, String category) {
		return dao.insertWord(conn, word, category);
	}

	public int insertCategory(Connection conn, String category, String word, String food) {
		return dao.insertCategory(conn, category, word, food);
	}

	public int insertFood(Connection conn, String food) {
		return dao.insertFood(conn, food);
	}

	public boolean searchFood(Connection conn, String food) {
		return dao.searchFood(conn, food);
	}

	// 모든 food word 반환;
	public List<String> searchAllFood(Connection conn) {
		return dao.searchAllFood(conn);
	}

	// words테이블의 모든 word, category 반환
	public List<WordCategory> searchAllWord(Connection conn) {
		return dao.searchAllWord(conn);
	}

	public int updateMatched(Connection conn, String category, String word, String food) {
		return dao.updateMatched(conn, category, word, food);
	}
}
