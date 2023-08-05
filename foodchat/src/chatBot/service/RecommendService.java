package chatBot.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chatBot.dao.ChatBotDAO;
import chatBot.model.WordCategory;
import chatBot.model.FoodCount;
import util.DBUtil;

public class RecommendService {
	ChatBotDAO dao = new ChatBotDAO();
	List<String> exceptionList = null;
	
	public String recommendFoodName(List<String> knownList) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<WordCategory> fcList = new ArrayList<>();
			for (String knownWord : knownList) {
				String category = dao.getCategory(conn, knownWord);
				fcList.add(new WordCategory(knownWord, category));
			}
			String foodName = dao.getFoodName(conn, fcList);
			return foodName;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	// excption테이블에 있는 필요없는 단어 제거해서 넘겨주기
	public List<String> removeException(List<String> list) {
		List<String> words = new ArrayList<>();
		List<String> exception = exceptionList;

		words.addAll(list);
		words.removeAll(exception);
		System.out.println(words);

		return words;
	}
}
