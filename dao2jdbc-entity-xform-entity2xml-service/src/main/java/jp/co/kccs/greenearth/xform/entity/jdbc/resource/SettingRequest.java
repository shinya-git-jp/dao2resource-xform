package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSettingImpl;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.jdbc.configuration.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SettingRequest {
	private Map<String, Object> transform = new HashMap<>();

	public Map<Class<?>, GXFormSetting> toDaoCommonSetting() {
		GTransformSetting transformSetting = getTransform(transform);
		return Map.of(
				GTransformSetting.class, transformSetting
		);
	}
	protected GTransformSetting getTransform(Map<String, Object> transformMap) {
		GTransformSetting transformSetting = new GTransformSettingImpl();
		if (transformMap.containsKey("encoding")) {
			transformSetting.setEncoding((String) transformMap.get("encoding"));
		}

		return transformSetting;
	}
}
