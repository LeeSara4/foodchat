package chatBot.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chatBot.dao.ChatBotDAO;
import chatBot.model.KnownWordList;
import util.DBUtil;

public class UnKnownService {
	KnownWordList knownList = new KnownWordList();

	public String unknownWord(List<String> wordList) {
		ChatBotDAO dao = new ChatBotDAO();

		Connection conn = null;
		try {
			conn = DBUtil.getConnection();

			List<String> unKnownList = dao.unknownWords(wordList, conn);
			wordList.removeAll(unKnownList);
			knownList.setKnownWordList(wordList);
			if (unKnownList.size() > 0) {
				return unKnownList.get(0);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn);
		}
		return null;
	}
}
