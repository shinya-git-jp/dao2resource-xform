package jp.co.kccs.greenearth.xform.code.jdbc.service.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jp.co.kccs.greenearth.commons.GFrameworkException;

import java.util.Map;

public class Utils {
	public static Map<String, Object> parseStringToMap(String object) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
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
	public static String createErrorResponse(Exception exception) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(Map.of("errorMessage: ", exception.getMessage()));
		} catch (JsonProcessingException e) {
			throw new GFrameworkException(e);
		}
	}
}
