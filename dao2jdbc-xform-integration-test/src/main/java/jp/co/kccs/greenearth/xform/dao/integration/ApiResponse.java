package jp.co.kccs.greenearth.xform.dao.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private Map<String, Object> error;
	private T data;

}
