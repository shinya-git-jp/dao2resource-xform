package jp.co.kccs.greenearth.xform.dao.integration;

import jp.co.kccs.greenearth.xform.dao.common.*;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GDbCommonSettingImpl;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GTransformSettingImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class SettingRequest {
	private Map<String, Object> db = new HashMap<>();
	private Map<String, Object> locale = new HashMap<>();
	private Map<String, Object> reservedWords = new HashMap<>();
	private Map<String, Object> transform = new HashMap<>();

	public GDaoCommonSetting toDaoCommonSetting() {
		return convert(Map.of(
				"db", db,
				"locale", locale,
				"reservedWords", reservedWords,
				"transform", transform
		));
	}
	protected GDaoCommonSetting convert(Map<String, Object> map) {
		GDaoCommonSetting commonSetting = new GDaoCommonSettingImpl();

		commonSetting.setReservedWords(convertReservedWords(map));
		commonSetting.setDb(convertDb(map));
		commonSetting.setLocale(convertLocale(map));
		commonSetting.setTransform(getTransform(map));
		return commonSetting;
	}
	protected GLocaleCommonSetting convertLocale(Map<String, Object> map) {
		GLocaleCommonSetting localeCommonSetting = new GLocaleCommonSettingImpl();
		if (map.containsKey("locale")) {
			map = (Map<String, Object>) map.get("locale");
			localeCommonSetting.setCurrent((String) map.get("current"));
			localeCommonSetting.setMapping(convertLocaleMapping(map));
		}
		return localeCommonSetting;
	}
	protected GLocaleMappingCommonSetting convertLocaleMapping(Map<String, Object> map) {
		GLocaleMappingCommonSetting localeMappingCommonSetting = new GLocaleMappingCommonSettingImpl();
		if (map.containsKey("mapping")) {
			Map<String, String> localeMappingMap = (Map<String, String>) map.get("mapping");
			localeMappingCommonSetting.setCountry1(localeMappingMap.get("country1"));
			localeMappingCommonSetting.setCountry2(localeMappingMap.get("country2"));
			localeMappingCommonSetting.setCountry3(localeMappingMap.get("country3"));
			localeMappingCommonSetting.setCountry4(localeMappingMap.get("country4"));
			localeMappingCommonSetting.setCountry5(localeMappingMap.get("country5"));
		}
		return localeMappingCommonSetting;
	}
	protected GReservedWordSetting convertReservedWords(Map<String, Object> map) {
		Map<String, Object> reservedWordMap = (Map<String, Object>) map.get("reservedWords");
		GReservedWordSetting reservedWordSetting = new GReservedWordSettingImpl();
		reservedWordSetting.setCompanyCodeColumn((String) reservedWordMap.get("companyCodeKey"));
		reservedWordSetting.setExclusiveKeyColumn((String) reservedWordMap.get("exclusiveKey"));
		reservedWordSetting.setPrimaryKeyColumn((String) reservedWordMap.get("primaryKey"));
		reservedWordSetting.setInsertedUserIdColumn((String) reservedWordMap.get("insertedUserIDKey"));
		reservedWordSetting.setInsertedDateColumn((String) reservedWordMap.get("insertedDateKey"));
		reservedWordSetting.setUpdatedUserIdColumn((String) reservedWordMap.get("updatedUserIDKey"));
		reservedWordSetting.setUpdatedDateColumn((String) reservedWordMap.get("updatedDateKey"));
		return reservedWordSetting;
	}
	protected GTransformSetting getTransform(Map<String, Object> parameter) {
		GTransformSettingImpl transformSetting = new GTransformSettingImpl();
		if (parameter.containsKey("transform")) {
			Map<String, Object> transformMap = (Map<String, Object>) parameter.get("transform");
			if (transformMap.containsKey("encoding")) {
				transformSetting.setEncoding((String) transformMap.get("encoding"));
			}
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
		}

		return transformSetting;
	}
	protected GDbCommonSetting convertDb(Map<String, Object> parameter) {
		Map<String, String> dbParameters = (Map<String, String>) parameter.get("db");
		GDbCommonSetting dbCommonSetting = new GDbCommonSettingImpl();
		if (Objects.nonNull(dbParameters.get("dbType"))) {
			dbCommonSetting.setDbType(GDaoDbType.valueOf(dbParameters.get("dbType")));
		}
		dbCommonSetting.setSchema(dbParameters.get("schema"));
		dbCommonSetting.setUsername(dbParameters.get("username"));
		dbCommonSetting.setPassword(dbParameters.get("password"));
		dbCommonSetting.setUrl(dbParameters.get("url"));
		return dbCommonSetting;
	}
}
