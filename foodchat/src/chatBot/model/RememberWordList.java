package chatBot.model;

import java.util.ArrayList;
import java.util.List;

public class RememberWordList {
	private static List<String> knownWordList = new ArrayList<String>();
	private static List<String> RefusalList = new ArrayList<String>();

	public static List<String> getKnownWordList() {
		return knownWordList;
	}

	public static void setKnownWordList(List<String> knownWordLists) {
		knownWordList = knownWordLists;
	}

	public static List<String> getRefusalList() {
		return RefusalList;
	}

	public static void setRefusalList(List<String> refusalList) {
		RefusalList = refusalList;
	}

	// 메소드 하나 작성해서 필드의 리스트에 어데드 파라미터 받아서 하는거 해서 모르는 단어 배웠을 때 배운단어로 처리하고, 실행 // 그리고
	// 거절단어 리스트 파라미터 더하기 만들기
	public static void addKnownWordList(String word) {
		knownWordList.add(word);
	}

	public static void addRefusalList(String word) {
		RefusalList.add(word);
		System.out.println("거절음식리스트 : " + RefusalList);
	}

	public static void deleteRefusalList(String word) {
		RefusalList.removeIf(words -> words.equals(word));
	}
}
