package chatBot.service;

import java.util.ArrayList;
import java.util.List;

public class ChatBotAnswerList {
	public static List<String> firstAnswer() {
		List<String> list = new ArrayList<String>();

		list.add("내가 추천하는 음식은 이거야!");
		list.add("이걸 먹어보는 건 어때?");
		list.add("그냥 이거 무조건 먹어라.");

		return list;
	}

	public static List<String> secondAnswer() {
		List<String> list = new ArrayList<String>();

		list.add("흠.. 그러면 이건 어때?");
		list.add("니 말을 알겠어. 이거 먹어");
		list.add("그냥 이거 무조건 먹어라.");

		return list;
	}
	
	public static List<String> thirdAnswer(){
		List<String> list = new ArrayList<String>();

		list.add("너 되게 까다롭구나?");
		list.add("제발 그냥 먹어라");
		list.add("그냥 이거 무조건 먹어라.");

		return list;
	}
	
}
