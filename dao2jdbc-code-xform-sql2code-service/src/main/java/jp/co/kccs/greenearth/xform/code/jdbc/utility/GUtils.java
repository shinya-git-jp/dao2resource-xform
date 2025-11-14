package jp.co.kccs.greenearth.xform.code.jdbc.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.kccs.greenearth.commons.GFrameworkException;

import java.util.Map;

public class GUtils {
	public static Map<String, Object> parseStringToMap(String object) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(object, Map.class);
		} catch (JsonProcessingException e) {
			throw new GFrameworkException(e);
		}
	}

	public static String convertObjectToString(Object object) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new GFrameworkException(e);
		}
	}

}
