package org.gradle.needle.verify;

import org.gradle.needle.client.HttpClientFactory;
import org.gradle.needle.util.JsonUtils;

import com.google.gson.JsonObject;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

/**
 * HTTPµÄ¸÷ÖÖ¶ÏÑÔ¼¯ºÏ
 * 
 * @author kongzhaolei
 *
 */
public class HttpResVer {

	// http ×´Ì¬Âë¶ÏÑÔ
	public static void verStatusCode(int code) {
		assertThat(code).isEqualTo(HttpClientFactory.getStatusCode());
	}

	// http ×Ö·û´®ÐòÁÐ¶ÏÑÔ
	public static void verContainsSequence(String res) {
		assertThat(res).containsSequence("{", "ModelData");
	}

	// http ¹Ø¼ü×Ö´ÎÊý¶ÏÑÔ
	public static void verKeyWordFrequency(String res, String keyword) {
		assertThat(res).matches("");

	}

	// http ¹Ø¼ü×ÖvalueºÏ¼Æ¶ÏÑÔ
	public static void verkeyWordSum(String res, String keyword) {
		Map<String, JsonObject> modeldata = JsonUtils.phareData(res);
		int sum = 0;
		for (String key : modeldata.keySet()) {
			JsonObject single = modeldata.get(key);
			sum = sum + single.get(keyword).getAsInt();

		}
	}
}
