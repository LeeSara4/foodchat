package chatBot.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import chatBot.dao.ChatBotDAO;
import chatBot.model.RememberWordList;
import chatBot.service.RecommendService;
import imgFinder.ImageReturner;
import util.DBUtil;
import util.Setting;

@WebServlet("/ask")
public class AskServlet extends HttpServlet {
	ChatBotDAO dao = new ChatBotDAO();
	RecommendService rs = new RecommendService();
	HashMap<String, Object> map = new HashMap<String, Object>();
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject answer;
		String food = requestReader(req);
		System.out.println("ask서블릿의 음식명 : " + food);
		List<String> words = foodChange3Words(food);
		String foodName = getFoodName(words);
		map.put("answer", foodName);
		map.put("img", ImageReturner.imageReturn(foodName));
		answer = new JSONObject(map);
		String text = String.valueOf(answer);
		Setting.resp(resp, 200, text);
	}

	private String getFoodName(List<String> words) {
		String foodName = rs.recommendFoodName(words);
		RememberWordList.addRefusalList(foodName);
		String foodName2 = rs.recommendFoodName(words);
		RememberWordList.deleteRefusalList(foodName);
		return foodName2;
	}

	private List<String> foodChange3Words(String food) {
		Connection conn = null;
		List<String> words = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection();
			List<String> list = dao.selectFoodByAssoiate(conn, food);
			for (String word : list) {
				words.add(word);
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		} finally {
			DBUtil.close(conn);
		}
		return words;
	}

	private String requestReader(HttpServletRequest req) throws IOException {
		// request json 형태로 오는 정보임
		StringBuilder sb = new StringBuilder();
		BufferedReader br = req.getReader();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String body = sb.toString();
		Pattern p = Pattern.compile("\\{\"ask\":\"(.+?)\"\\}");
		Matcher m = p.matcher(body);
		m.find();
		String chat = m.group(1);
		return chat;
	}

}
