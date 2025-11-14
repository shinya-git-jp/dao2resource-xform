package jp.co.kccs.greenearth.xform.dao.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CodeResponse {
	private String description;
	private String type;
	private String sqlScript;
	private Map<String, String> apiCodes;
	private Map<String, Object> virtualEntity;

}
