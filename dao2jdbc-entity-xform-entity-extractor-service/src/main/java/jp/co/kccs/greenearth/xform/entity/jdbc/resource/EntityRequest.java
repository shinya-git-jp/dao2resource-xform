package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntityRequest {
	private SettingRequest setting;
	private Map<String, Object> value;
}
