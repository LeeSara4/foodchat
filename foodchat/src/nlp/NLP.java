package nlp;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;

public class NLP {

	public NLP() {
	}

	// 명사의 형태를 가지는 애들 걸러내기
	private static boolean regexN(String pos) {
		String pattern = "NNG|NNP|MAG|VA|XR";
		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(pos);

		return matcher.matches();
	}

	// 연결어미만 잘라내기 (미실행)
	private static boolean regexJ(String pos) {
		String pattern = "JKS|JKC|JKG|JKO|JKB|JKV|JKQ|JX|JC|EP|EF|EC|ETN|ETM";
		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(pos);

		return matcher.matches();
	}
	
	// (미실행)
	private static List<String> 어미붙이기(List<Token> list) {
		List<String> mergedList = new ArrayList<>();
		List<String> morphList = new ArrayList<>();
		String empty = "";
		int mergedCount = 0;

		// regexJ가 트루일때만 바로 앞에 녀석과 합치기
		for (Token token : list) {
			morphList.add(token.getMorph());

			if (regexJ(token.getPos())) {
				mergedCount--;
				String target = mergedList.get(mergedCount);
				String result = target + token.getMorph();
				mergedList.remove(mergedCount);
				mergedList.add(result);
				mergedCount++;
			} else {
				mergedList.add(token.getMorph());
				mergedCount++;
			}
		}
		return mergedList;
	}

//	현재의 형태로는 한계가 존재
//	=> 자연어 -> 컴퓨터어 가능
//	but => 컴퓨터어 -> 자연어 불가
//
//	=> 따라서 모르는 단어 발생시 아래의 형태로 질문 구조 작성(임시방편)
//	VA는 ~다의 형태로 되묻기
//	나머지는 ~이(가) 형태로 되묻기 가능

	public static List<String> doNLP(String text) {
		Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
//		komoran.setUserDic("user.dic");
		String userInputText = text;
		String trimText = userInputText.trim();
		Set<String> resultSet = new LinkedHashSet<>();
		List<String> resultList = new ArrayList<>();

		// 형태소만 보내는주는 애
//		List<String> komoList = komoran.analyze(userText).getNouns();
//		for(String token : komoList) {
//			System.out.println(token);
//		}
//		System.out.println(komoList);

		// 토큰을 참조하는 형태소와 품사
		List<Token> tokens = komoran.analyze(trimText).getTokenList();
		for (Token token : tokens) {

			if (regexN(token.getPos())) {
//			System.out.println("문자열 : " + token.getMorph());
				resultSet.add(token.getMorph());
//			System.out.println("품사 : " + token.getPos());
			}
		}

		for (String s : resultSet) {
			resultList.add(s);
		}

		System.out.println("리스트 출력이요 : " + resultList);

		return resultList;
	}
}
