package chatBot.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chatBot.model.KnownWordList;
import chatBot.service.InsertService;
import chatBot.service.UnKnownService;
import chatBot.service.RecommendService;
import nlp.NLP;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
	UnKnownService us = new UnKnownService();
	RecommendService rs = new RecommendService();
	InsertService is = new InsertService();
	KnownWordList knownWordList = new KnownWordList();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(200);
		resp.setHeader("Content-Type", "application/json;charset=utf-8");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		splitString(req, resp);
	}
	
	public void insert(String requestData) {
		is.insert(requestData);
	}

	public String foodName(List<String> knownList) {
		String foodName = rs.recommendFoodName(knownList);
		return foodName;
	}

	public void splitString(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = req.getReader();
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
		System.out.println(chat);

		resp.setStatus(200);
		resp.setHeader("Content-Type", "application/json;charset=utf-8");
		resp.getWriter().write("{\"request\": \"" + chat + "\"}");
	}
}
