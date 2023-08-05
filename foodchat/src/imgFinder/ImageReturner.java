package imgFinder;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageReturner {

	public ImageReturner() {
	};

	public static String imageReturn(String name) {
		Gson gson = new Gson();
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://dapi.kakao.com/v2/search/image?query=" + name + "&page=1&size=2")
				.addHeader("Authorization", "KakaoAK 16a31b75ebda66fb5ccb58afcb8d9bc1").get().build();

		Call call = client.newCall(request);
		try (Response resp = call.execute()) {
			int code = resp.code();
			System.out.println(code);
			if (code == 200) {
				System.out.println("OK");

				String body = resp.body().string();
				System.out.println(body);

				try {
//					image_url 만 뽑아내기
					JSONParser parser = new JSONParser();

					JSONObject jsonObject = (JSONObject) parser.parse(body);
					System.out.println("body를 파싱한 값: " + jsonObject);

					JSONArray jsonArray = (JSONArray) jsonObject.get("documents");
					System.out.println("documents의 값: " + jsonArray.get(0));

					JSONObject finaljson = (JSONObject) jsonArray.get(1);
					System.out.println("image_URL: " + finaljson.get("image_url"));

					String result = finaljson.get("image_url").toString();
					return result;
//					System.out.println(jsonObject.get("documents"));
//					Map<String, String> aa = (HashMap) jsonObject.get("documents");
//					System.out.println(aa.get("doc_url"));

				} catch (ParseException e) {
					e.printStackTrace();
				}

			} else {
				System.out.println("aa");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}