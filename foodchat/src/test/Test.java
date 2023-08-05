package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import chatBot.dao.ChatBotDAO;
import chatBot.service.RecommendService;
import util.DBUtil;

public class Test {
	public static void main(String[] args) {
		ChatBotDAO dao = new ChatBotDAO();
		Connection conn = null;
		List<String> words = new ArrayList<String>();
		words.add("아기");
		words.add("달달");
		words.add("시원");
		List<String> list = null;
		try {
			conn = DBUtil.getConnection();
			list = dao.unknownWords(words, conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(conn);
		}
		System.out.println(list);
		
	}
}


