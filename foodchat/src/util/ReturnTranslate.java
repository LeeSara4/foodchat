package util;

public class ReturnTranslate {
	public static String Translate(String target) {
		if (target.equals("단어")) {
			return "associate";
		} else if (target.equals("음식")) {
			return "food";
		} else if (target.equals("예외")) {
			return "exceptions";
		}

		return "잘못된거임";
//	weather 날씨
//	place 장소
//	ingredient 재료
//	person 사람
//	action 행동
// "사람", "날씨", "장소", "재료", "행동", "맛", "음식"
	}
}
