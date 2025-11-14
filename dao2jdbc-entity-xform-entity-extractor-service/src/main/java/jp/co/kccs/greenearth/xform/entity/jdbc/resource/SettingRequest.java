package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import jp.co.kccs.greenearth.xform.configuration.*;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.dao.common.*;
import jp.co.kccs.greenearth.xform.jdbc.configuration.*;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import jp.co.kccs.greenearth.xform.old.configuration.*;
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

	public Map<Class<?>, GXFormSetting> toDaoCommonSetting() {
		return Map.of(
				GDbCommonSetting.class, getDbSetting(db),
				GLocaleCommonSetting.class, getLocaleSetting(locale),
				GReservedWordSetting.class, getReservedWordSetting(reservedWords),
				GTransformSetting.class, getTransformSetting(transform)
		);
	}
	protected GLocaleCommonSetting getLocaleSetting(Map<String, Object> map) {
		GLocaleCommonSetting localeCommonSetting = new GLocaleCommonSettingImpl();
		localeCommonSetting.setCurrent((String) map.get("current"));
		localeCommonSetting.setMapping(getLocaleMappingSetting(map));
		return localeCommonSetting;
	}
	protected GLocaleMappingCommonSetting getLocaleMappingSetting(Map<String, Object> map) {
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
	protected GReservedWordSetting getReservedWordSetting(Map<String, Object> reservedWordMap) {
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

	protected GTransformSetting getTransformSetting(Map<String, Object> transformMap) {
		GTransformSetting transformSetting = new GTransformSettingImpl();
		if (transformMap.containsKey("encoding")) {
			transformSetting.setEncoding((String) transformMap.get("encoding"));
		}
		return transformSetting;
	}
	protected GDbCommonSetting getDbSetting(Map<String, Object> dbParameters) {
		GDbCommonSetting dbCommonSetting = new GDbCommonSettingImpl();
		if (Objects.nonNull(dbParameters.get("dbType"))) {
			dbCommonSetting.setDbType(GDaoDbType.valueOf((String) dbParameters.get("dbType")));
		}
		dbCommonSetting.setSchema((String) dbParameters.get("schema"));
		dbCommonSetting.setUsername((String) dbParameters.get("username"));
		String password = (String) dbParameters.get("password");
		dbCommonSetting.setPassword(password);
		dbCommonSetting.setUrl((String) dbParameters.get("url"));
		return dbCommonSetting;
	}
}
