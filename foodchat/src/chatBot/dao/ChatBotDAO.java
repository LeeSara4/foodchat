package chatBot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chatBot.model.FoodCount;
import chatBot.model.RememberWordList;
import chatBot.model.WordCategory;
import util.DBUtil;

public class ChatBotDAO {
	public String getFoodName(Connection conn, List<WordCategory> wcList) throws SQLException {
		List<String> excludeList = RememberWordList.getRefusalList();
		String exclude = "";
		if (excludeList.size() >= 1) {
			for (int i = 0; i < excludeList.size(); i++) {
				exclude += "AND food != ('" + excludeList.get(i) + "')";
			}
		}
		System.out.println("dao에서 wcList : " + wcList);

		PreparedStatement stmt = null;
		ResultSet rs = null;

		String words = "";
		String word = wcList.get(0).getWord();
		words += "'"+word+"'";
		for (int i = 1; i < wcList.size(); i++) {
			word = wcList.get(i).getWord();
			words += ", '" + word + "'";
		}
		try {

			String li = "SELECT food, MAX(total_count) AS max_total_count\r\n" + "FROM (\r\n"
					+ "    SELECT food, SUM(count) AS total_count\r\n" + "    FROM associate\r\n"
					+ "    WHERE word IN (" + words + ") " + exclude + "    GROUP BY food\r\n"
					+ ") AS grouped_data\r\n" + "GROUP BY food\r\n" + "ORDER BY max_total_count DESC\r\n"
					+ "LIMIT 1;";

			System.out.println("dao에서 쿼리문 : " + li);
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

	public List<String> getExceptions(Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();

		try {
			stmt = conn.prepareStatement("SELECT * FROM exceptions;");
			rs = stmt.executeQuery();
			while (rs.next()) {
				String word = rs.getString("word");
				list.add(word);
			}
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return list;
	}

	public int insertWord(Connection conn, String word, String category) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("insert into words (word, category) values (?,?);");
			stmt.setString(1, word);
			stmt.setString(2, category);
			int result = stmt.executeUpdate();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally

		{
			DBUtil.close(stmt);
		}
		return -1;
	}

	public int insertCategory(Connection conn, String category, String word, String food) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("insert into " + category + " (word, food, count) values (?,?,100);");
//			stmt.setString(1, category);
			stmt.setString(1, word);
			stmt.setString(2, food);
			int result = stmt.executeUpdate();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally

		{
			DBUtil.close(stmt);
		}
		return -1;
	}

	public void updateByCount(Connection conn, int count, String category, String word, String food) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(
					"UPDATE " + category + " SET `count` = (`count` + ?) WHERE (`word` = ?) and (`food` = ?);");
			stmt.setInt(1, count);
			stmt.setString(2, word);
			stmt.setString(3, food);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(stmt);
		}
	}

	public boolean searchFood(Connection conn, String food) {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareStatement("SELECT * FROM food where food = ?;");
			stmt.setString(1, food);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return false;
	}

	public List<String> searchAllFood(Connection conn) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();

		try {
			stmt = conn.prepareStatement("SELECT food FROM foodchat.food");
			rs = stmt.executeQuery();
			while (rs.next()) {
				String word = rs.getString("food");
				list.add(word);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return list;
	}

	public List<WordCategory> searchAllWord(Connection conn) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<WordCategory> list = new ArrayList<>();

		try {
			stmt = conn.prepareStatement("SELECT * FROM foodchat.words;");
			rs = stmt.executeQuery();
			while (rs.next()) {
				String word = rs.getString("word");
				String category = rs.getString("category");
				WordCategory wordAndCategory = new WordCategory(word, category);
				list.add(wordAndCategory);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return list;
	}

	public int insertFood(Connection conn, String food) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("insert into food (food) values (?);");
			stmt.setString(1, food);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally

		{
			DBUtil.close(stmt);
		}
		return -1;
	}

	public int insertException(Connection conn, String word) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("INSERT INTO `foodchat`.`exceptions` (`word`) VALUES (?);");
			stmt.setString(1, word);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally

		{
			DBUtil.close(stmt);
		}
		return -1;
	}

	public int updateMatched(Connection conn, String category, String word, String food) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("update " + category + " set count = count + 10 where word = ? and food = ?;");
			stmt.setString(1, word);
			stmt.setNString(2, food);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally

		{
			DBUtil.close(stmt);
		}
		return -1;
	}

	public List<String> searchNegativeWord(List<String> list) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<>();

		try {
			conn = DBUtil.getConnection();
			for (String s : list) {

				stmt = conn.prepareStatement("Select * from negative where word = ?");
				stmt.setString(1, s);
				rs = stmt.executeQuery();

				if (rs.next()) {
					result.clear();
				} else {
					result.add(s);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally

		{
			DBUtil.close(rs);
			DBUtil.close(stmt);
			DBUtil.close(conn);
		}
		return result;
	}

	public List<String> selectFoodByAssoiate(Connection conn, String food) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<>();

		try {
			stmt = conn.prepareStatement("select * from associate where food = ? order by count DESC limit 3;");
			stmt.setString(1, food);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String word = rs.getString("word");
				list.add(word);
			}
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return list;
	}
}
