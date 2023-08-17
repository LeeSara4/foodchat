package chatBot.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import chatBot.dao.ChatBotDAO;
import chatBot.model.RememberWordList;
import util.DBUtil;

public class UpdateService {
	ChatBotDAO dao = new ChatBotDAO();

	public void updateByCount(Connection conn, int count, String food) throws SQLException {
		List<String> knownWords = RememberWordList.getKnownWordList();
		for (String word : knownWords) {
			String category = dao.getCategory(conn, word);
			dao.updateByCount(conn, count, category, word, food);
		}
	}
}
