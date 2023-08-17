package nlp;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatBot.dao.ChatBotDAO;
import chatBot.servlet.ChatServlet;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;

public class NLP {
	// 코모란 생성
	private static Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
	private static ChatBotDAO dao = new ChatBotDAO();

	public NLP() {
	}

	// 명사의 형태를 가지는 애들 걸러내기
	private static boolean regexN(String pos) {
		String pattern = "NNG|NNP|MAG|VA|XR";
		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(pos);

		return matcher.matches();
	}

	// 부정문을 먼저 확인하고 , 리스트를 반환
	private static List<String> negativeNNP(String text) {

//		komoran.setUserDic(ChatServlet.filepath);
//		komoran.setUserDic("negative.dic");

		// 결론 파라미터에 그냥 절대경로 써서 사용하자.... 이건 방법이없다.... 넘무 어려워
		komoran.setUserDic("C://Users//GGG//git//foodchat//negative.dic");

		List<String> list = new ArrayList<>();
		String userInputText = text;
		String trimText = userInputText.trim();

		List<Token> tokens = komoran.analyze(trimText).getTokenList();
		for (Token token : tokens) {

			if (regexN(token.getPos())) {
				System.out.println("문자열 : " + token.getMorph());
				System.out.println("품사 : " + token.getPos());
				list.add(token.getMorph());
			}
		}

		List<String> result = dao.searchNegativeWord(list);

		return result;
	}

//	현재의 형태로는 한계가 존재
//	=> 자연어 -> 컴퓨터어 가능
//	but => 컴퓨터어 -> 자연어 불가
//
//	=> 따라서 모르는 단어 발생시 아래의 형태로 질문 구조 작성(임시방편)
//	VA는 ~다의 형태로 되묻기
//	나머지는 ~이(가) 형태로 되묻기 가능

	public static List<String> doNLP(String text) {
		List<String> resultSet = negativeNNP(text);
		List<String> resultList = new ArrayList<>();

		for (String s : resultSet) {
			resultList.add(s);
		}

		System.out.println("단어 리스트 : " + resultList);

		return resultList;
	}
}
