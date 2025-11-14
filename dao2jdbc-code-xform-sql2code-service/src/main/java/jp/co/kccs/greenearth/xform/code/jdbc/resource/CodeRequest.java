package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodeRequest {
	private SettingRequest setting;
	private Map<String, Object> value;
}
