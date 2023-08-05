package chatBot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chatBot.model.WordCategory;
import chatBot.model.FoodCount;
import util.DBUtil;

public class ChatBotDAO {
	public String getFoodName(Connection conn, List<WordCategory> fcList) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String plusSQL = "";
			String union = "UNION ALL ";
			for (int i = 0; i < fcList.size(); i++) {
				String tableName = fcList.get(i).getCategory();
				String word = "'"+fcList.get(i).getWord()+"'";
				if (i != 0) {
					plusSQL += union;
				}
				plusSQL += "SELECT food, count FROM " + tableName + " where word = " + word;
			}
			
			String li = "SELECT food, SUM(count) AS result FROM ("+plusSQL+") AS combined_table GROUP BY food order by result DESC LIMIT 1;";

			stmt = conn.prepareStatement(li);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String food = rs.getString("food");
				return food;
			}
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return null;
	}

	// 아는 단어인지 모르는 단어인지 체크하고 모르는 단어만 리스트로써 반환하는 메소드
	// 물어볼 단어만 반환
	public List<String> unknownWords(List<String> words, Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();

		try {
			for (String word : words) {
				stmt = conn.prepareStatement("SELECT * FROM foodchat.words WHERE word = ?;");
				stmt.setString(1, word);
				rs = stmt.executeQuery();
				if (!rs.next()) {
					list.add(word);
				}
			}
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return list;
	}

	public String getCategory(Connection conn, String knownWord) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<FoodCount> list = new ArrayList<>();

		try {
			stmt = conn.prepareStatement("SELECT * FROM foodchat.words where word = ?;");
			stmt.setString(1, knownWord);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String category = rs.getString("category");
				return category;
			}
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return null;
	}

	public void insertWord(String requestData) {
		// TODO Auto-generated method stub
		
	}
	
	
}
