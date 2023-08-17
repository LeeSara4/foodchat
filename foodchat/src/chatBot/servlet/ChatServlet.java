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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import chatBot.dao.ChatBotDAO;
import chatBot.model.RememberWordList;
import chatBot.model.WordCategory;
import chatBot.service.Divide;
import chatBot.service.InsertService;
import chatBot.service.RecommendService;
import chatBot.service.UnKnownService;
import chatBot.service.UpdateService;
import imgFinder.ImageReturner;
import nlp.NLP;
import util.DBUtil;
import util.ReturnTranslate;
import util.Setting;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
	UnKnownService us = new UnKnownService();
	RecommendService rs = new RecommendService();
	InsertService is = new InsertService();
	ChatBotDAO dao = new ChatBotDAO();
	UpdateService updateS = new UpdateService();
	Divide d = new Divide();

//  배포시 절대경로 찾기위한 메소드 인데 먼가 잘 안된다... 나중에 참고 가능성이 있으니 남겨둠
//	public static String filepath;
//	@Override
//	public void init(ServletConfig config) throws ServletException {
//		filepath = config.getServletContext().getRealPath("negative.dic");
//	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processDoGoet(resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String body = requestReader(req);
		JSONParser parser = new JSONParser();

		Connection conn = null;
		try {
			conn = DBUtil.getConnection();

//			 conn.setAutoCommit(false);
			JSONArray jsonArr = (JSONArray) parser.parse(body);
			System.out.println(jsonArr.size());
			// word + category words에 넣어주기

			// category에
			// word + food

			// String word
			System.out.println(jsonArr.get(0));
			JSONObject word = (JSONObject) jsonArr.get(0);
			String strWord = word.get("request").toString();
			System.out.println("word: " + strWord);

			// String category
			System.out.println(jsonArr.get(1));
			JSONObject cate = (JSONObject) jsonArr.get(1);
			String category = cate.get("category").toString();
			System.out.println("category: " + category);

			if (category.equals("거절")) {
				processDoPutByRefusal(conn, strWord);
			} else if (category.equals("수락")) {
				processDoPutByAccept(conn, strWord);
			} else if (category.equals("음식")) {
				processDoPutByFood(conn, strWord, category);
			} else if (category.equals("예외")) {
				// 작성중
				processDoPutByExceptionWord(conn, strWord);
			} else if (category.equals("단어")) { // 거절, 수락, 음식 아니면 정보 저장후에 음식명 출력해야함
				processDoPutBySave(conn, strWord, category, jsonArr);
			} else {
				System.out.println("두포스트에 잘못된 작동");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HashMap<String, Object> map = new HashMap<String, Object>();
		JSONObject answer = null;
		List<String> chat = splitString(req);

		if (chat == null) {
			map.put("answer", "");
			answer = new JSONObject(map);
			String text = String.valueOf(answer);
			Setting.resp(resp, 200, text);
		} else {
			System.out.println("chat : " + chat);
			// chat : 따뜻 시원 한강 냉면
			// 코드생성 = 단어와 음식을 구분해서 저장
			List<String> words = new ArrayList<String>();
			List<String> foods = new ArrayList<String>();
			divide(chat, words, foods);

			// 단어리스트 0 이면서 음식리스트 1이면 js에 해당음식 지도냐 어울리는 음식추천이냐 질문하는 정보 전달
			if (foods.size() == 1 && words.size() == 0) {
				String food = foods.get(0);
				String text = "{\"ask\": \"" + food + "\"}";
				Setting.resp(resp, 200, text);
			} else if (words.size() != 0 || foods.size() != 0) {
				// 음식을 단어 3개로 변환하는 메소드 만들기
				foodChange3Words(foods, words);
				// chat을 자연어 처리해서 wordList로 넣는다
				String unknownWord = us.unknownWord(words); // 단어 리스트를 넣어서 모르는 단어 하나를 받는다
				if (unknownWord != null) { // 모르는 단어가 있을 때 - 모르는 단어가 없으면 null을 반환해서 조건처리한다
					String json = "{\"request\": \"" + unknownWord + "\"}";
					Setting.resp(resp, 200, json);
				} else { // 모르는 단어가 없을 때 - unknownWord 가 null 이면 모르는 단어가 없으므로 음식명을 반환한다.
					System.out.println("두포스트에서 모르는단어 없을때 : " + RememberWordList.getKnownWordList());
					String foodName = foodName(RememberWordList.getKnownWordList());
					map.put("answer", foodName);
					map.put("img", ImageReturner.imageReturn(foodName));
					answer = new JSONObject(map);
					String text = String.valueOf(answer);
					Setting.resp(resp, 200, text);
				}
			} else {
				System.out.println("서블릿에서 문제가 있습니다.");
			}
		}
	}

	private void foodChange3Words(List<String> foods, List<String> words) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			for (String food : foods) {
				List<String> list;
				list = dao.selectFoodByAssoiate(conn, food);
				for (String word : list) {
					words.add(word);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		} finally {
			DBUtil.close(conn);
		}
	}

	private void divide(List<String> chat, List<String> words, List<String> foods) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			d.divide(conn, chat, words, foods);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn);
		}
	}

	private void processDoPutByExceptionWord(Connection conn, String strWord) {
		System.out.println("두풋 예외단어 저장");
		dao.insertException(conn, strWord);
	}

	private void processDoPutBySave(Connection conn, String strWord, String category, JSONArray jsonArr) {
		System.out.println("두풋 저장방식");
		// words 에 넣어주기
		String categoryT = ReturnTranslate.Translate(category);
		System.out.println("번역된 카테고리" + categoryT);
		// 영어로 번역
		is.insert(conn, strWord, categoryT);

		// 해당 카테고리에 word, food 넣어주기

		// 새로운 단어 학습할때 해당카데고리에 모든 음식을 추가해주기

		// 메소드 food 테이블 접근해서 모든 food 가지는 list<String> 만들기

		// food 테이블 접근해서 word, 모든 food 넣어주고
		// 대화를 통해서 사용자가 선택한 food는 중복검사해서
		// 없으면 넣어주기 -> 완전 새로운 food일 경우니까 word접근해서 모든 테이블에 행으로 추가해주기

		// String refer foods
		// 먼저 food에 있는거 다때려 박고
		List<String> foodList = is.searchAllFood(conn);
		System.out.println("현재 모든 푸드리스트" + foodList.toString());
		for (String str : foodList) {
			is.insertCategory(conn, categoryT, strWord, str);
		}
		// 중복검사해서 있는거 카운트++, 없는거는 추가해주는데 새로운 food니까 food에 넣고, words에 접근해서 모든행들에 새로운
		// food넣어주기
		for (int i = 2; i < jsonArr.size(); i++) {

			System.out.println(jsonArr.get(i));
			JSONObject food = (JSONObject) jsonArr.get(i);
			String foodTarget = food.get("food").toString();

			if (!is.searchFood(conn, foodTarget)) {
				System.out.println("food: " + foodTarget);
				// food에 해당 새로운 음식인 foodTarget 넣어주고
				is.insertFood(conn, foodTarget);
				// 모든 words의 word와 category 가져와서 해당카데고리에
				// words foodTarget 넣어주기
				List<WordCategory> wordList = is.searchAllWord(conn);
				for (WordCategory wc : wordList) {
					System.out.println("현재 words" + wc.getWord());
					String currentWord = wc.getWord();
					System.out.println("현재 words의 카테고리" + wc.getCategory());
					String currentCategory = wc.getCategory();
					is.insertCategory(conn, currentCategory, currentWord, foodTarget);
				}
			}
		}
	}

	private void processDoPutByFood(Connection conn, String strWord, String category) {
		System.out.println("두풋 음식방식");
		// 모든 words의 word와 category 가져와서 해당카데고리에
		// words foodTarget 넣어주기
		// words 에 넣어주기
		String categoryT = ReturnTranslate.Translate(category);
		System.out.println("번역된 카테고리" + categoryT);
		is.insertFood(conn, strWord);
		List<WordCategory> wordList = is.searchAllWord(conn);
		System.out.println("wordList : " + wordList);
		for (WordCategory wc : wordList) {
			System.out.println("현재 words" + wc.getWord());
			String currentWord = wc.getWord();
			System.out.println("현재 words의 카테고리" + wc.getCategory());
			String currentCategory = wc.getCategory();
			is.insertCategory(conn, currentCategory, currentWord, strWord);
		}
	}

	private void processDoPutByAccept(Connection conn, String strWord) throws SQLException {
		System.out.println("두풋 수락방식");
		updateS.updateByCount(conn, 1, strWord);
	}

	private void processDoPutByRefusal(Connection conn, String strWord) throws SQLException {
		System.out.println("두풋 거절방식");
		updateS.updateByCount(conn, -1, strWord);
		RememberWordList.addRefusalList(strWord);
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
		System.out.println(body);

		return body;
	}

//	public void insert(String requestData) {
//		is.insert(requestData);
//	}

	public String foodName(List<String> knownList) {
		if (!knownList.isEmpty()) {
			String foodName = rs.recommendFoodName(knownList);
			return foodName;
		} else {
			return "";
		}
	}

	public List<String> splitString(HttpServletRequest req) throws IOException {
		List<String> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		BufferedReader br = req.getReader();
		NLP nlp = new NLP();

		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String body = sb.toString();
		System.out.println("사용자 요청 body 확인: " + body);

		Pattern p = Pattern.compile("\\{\"chat\":\"(.+?)\"\\}");
		Matcher m = p.matcher(body);
		m.find();
		String chat = m.group(1);

		list = nlp.doNLP(chat);
		list = rs.removeException(list);
		return list;
	}

	private void processDoGoet(HttpServletResponse resp) throws IOException {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<String> foodList = is.searchAllFood(conn);
			map.put("list", foodList);
			JSONObject list = new JSONObject(map);
			Setting.resp(resp, 200, String.valueOf(list));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} finally {
			DBUtil.close(conn);
		}
	}
}
