package chatBot.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chatBot.dao.ChatBotDAO;
import chatBot.model.RememberWordList;
import util.DBUtil;

public class UnKnownService {

	public String unknownWord(List<String> wordList) {
		ChatBotDAO dao = new ChatBotDAO();

		Connection conn = null;
		try {
			conn = DBUtil.getConnection();

			List<String> unKnownList = dao.unknownWords(wordList, conn);
			wordList.removeAll(unKnownList);
			RememberWordList.setKnownWordList(wordList);
			System.out.println("ai에게 기억시키는 단어들 : " + RememberWordList.getKnownWordList());
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
