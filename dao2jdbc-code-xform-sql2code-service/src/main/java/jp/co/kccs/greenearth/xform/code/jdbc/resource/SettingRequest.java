package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jp.co.kccs.greenearth.xform.code.jdbc.configuration.GCodeTransformSetting;
import jp.co.kccs.greenearth.xform.code.jdbc.configuration.GCodeTransformSettingImpl;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSettingImpl;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
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
	private Map<String, Object> codeTransform = new HashMap<>();

	public Map<Class<?>, GXFormSetting> toDaoCommonSetting() {
		GTransformSetting transformSetting = getTransform(transform);
		GCodeTransformSetting codeTransformSetting = getCodeTransform(codeTransform);
		return Map.of(
				GTransformSetting.class, transformSetting,
				GCodeTransformSetting.class, codeTransformSetting
		);
	}
	protected GTransformSetting getTransform(Map<String, Object> transformMap) {
		GTransformSetting transformSetting = new GTransformSettingImpl();
		if (transformMap.containsKey("encoding")) {
			transformSetting.setEncoding((String) transformMap.get("encoding"));
		}

		return transformSetting;
	}
	protected GCodeTransformSetting getCodeTransform(Map<String, Object> transformMap) {
		GCodeTransformSetting transformSetting = new GCodeTransformSettingImpl();
		if (transformMap.containsKey("useForeignKey")) {
			boolean joinMode = (boolean) transformMap.get("useForeignKey");
			transformSetting.setUseForeignKey(joinMode);
		}
		if (transformMap.containsKey("forceAliasColumn")) {
			boolean forceAliasColumn = (boolean) transformMap.get("forceAliasColumn");
			transformSetting.setForceAliasColumn(forceAliasColumn);
		}
		if (transformMap.containsKey("entityQuery")) {
			boolean queryMode = (boolean) transformMap.get("entityQuery");
			transformSetting.setEntityQuery(queryMode);
		}
		if (transformMap.containsKey("useExpMap")) {
			boolean useExpMap = (boolean) transformMap.get("useExpMap");
			transformSetting.setUseExpMap(useExpMap);
		}
		return transformSetting;
	}
}
