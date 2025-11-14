package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import jp.co.kccs.greenearth.commons.GFrameworkContext;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SettingRequestTest {

	/**
	 * [1] 設定された値で変換される場合、変換された{@link GCommonSetting}の値が正しいであること。<br>
	 * [2] {@link SettingRequest}がデフォルトの状態の場合、変換された{@link GCommonSetting}の値がデフォルトであること.
	 */
	@Test
	public void testConvertToCommonSetting() {
		{
			Map<String, Object> transform = new HashMap<>();
			transform.put("encoding", "UTF-16");
			transform.put("useForeignKey", true);
			transform.put("forceAliasColumn", false);
			transform.put("entityQuery", false);
			transform.put("useExpMap", true);

			Map<String, Object> db = new HashMap<>();
			db.put("url", "testUrl");
			db.put("schema", "testSchema");
			db.put("username", "testUsername");
			db.put("password", "testPassword");
			db.put("dbType", "oracle");

			Map<String, Object> reservedWords = new HashMap<>();
			reservedWords.put("primaryKey", "primaryKey");
			reservedWords.put("exclusiveKey", "exclusiveKey");
			reservedWords.put("companyCodeKey", "companyCodeKey");
			reservedWords.put("insertedUserIDKey", "insertedUserIDKey");
			reservedWords.put("insertedDateKey", "insertedDateKey");
			reservedWords.put("updatedUserIDKey", "updatedUserIDKey");
			reservedWords.put("updatedDateKey", "updatedDateKey");

			Map<String, Object> locale = new HashMap<>();
			locale.put("current", "testCurrent");
			locale.put("mapping", Map.of(
					"country1", "en",
					"country2", "jp",
					"country3", "cn"
			));

			SettingRequest settingRequest = new SettingRequest();
			settingRequest.setTransform(transform);
			settingRequest.setDb(db);
			settingRequest.setLocale(locale);
			settingRequest.setReservedWords(reservedWords);
			Map<Class<?>, GXFormSetting> commonSetting = settingRequest.toDaoCommonSetting();
			assertEquals("UTF-16", ((GTransformSetting)commonSetting.get(GTransformSetting.class)).getEncoding());

			assertEquals("testUrl", ((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getUrl());
			assertEquals(GDaoDbType.oracle, ((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getDbType());
			assertEquals("testSchema", ((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getSchema());
			assertEquals("testUsername", ((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getUsername());
			assertEquals("testPassword", ((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getPassword());

			assertEquals("primaryKey", ((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getPrimaryKeyColumn());
			assertEquals("companyCodeKey", ((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getCompanyCodeColumn());
			assertEquals("exclusiveKey", ((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getExclusiveKeyColumn());
			assertEquals("insertedUserIDKey", ((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getInsertedUserIdColumn());
			assertEquals("insertedDateKey", ((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getInsertedDateColumn());
			assertEquals("updatedUserIDKey", ((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getUpdatedUserIdColumn());
			assertEquals("updatedDateKey", ((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getUpdatedDateColumn());

			assertEquals("testCurrent", ((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getCurrent());
			assertEquals("en",  ((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry1());
			assertEquals("jp",  ((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry2());
			assertEquals("cn",  ((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry3());
			assertNull( ((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry4());
			assertNull( ((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry5());
		}
		{
			SettingRequest settingRequest = new SettingRequest();
			Map<Class<?>, GXFormSetting> commonSetting = settingRequest.toDaoCommonSetting();
			assertEquals("UTF-8", ((GTransformSetting)commonSetting.get(GTransformSetting.class)).getEncoding());

			assertNull(((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getUrl());
			assertNull(((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getDbType());
			assertNull(((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getSchema());
			assertNull(((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getUsername());
			assertNull(((GDbCommonSetting)commonSetting.get(GDbCommonSetting.class)).getPassword());

			assertNull(((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getPrimaryKeyColumn());
			assertNull(((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getCompanyCodeColumn());
			assertNull(((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getExclusiveKeyColumn());
			assertNull(((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getInsertedUserIdColumn());
			assertNull(((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getInsertedDateColumn());
			assertNull(((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getUpdatedUserIdColumn());
			assertNull(((GReservedWordSetting)commonSetting.get(GReservedWordSetting.class)).getUpdatedDateColumn());

			assertEquals(GFrameworkContext.getInstance().getLocale().getLanguage(), ((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getCurrent());
			assertNull(((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry1());
			assertNull(((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry2());
			assertNull(((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry3());
			assertNull(((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry4());
			assertNull(((GLocaleCommonSetting)commonSetting.get(GLocaleCommonSetting.class)).getMapping().getCountry5());
		}
	}
}
