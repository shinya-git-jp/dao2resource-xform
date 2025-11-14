package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
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
			Map<Class<?>, GXFormSetting> commonSetting = settingRequest.toDaoCommonSetting();
			assertEquals("UTF-16", ((GTransformSetting)commonSetting.get(GTransformSetting.class)).getEncoding());


		}
		{
			SettingRequest settingRequest = new SettingRequest();
			Map<Class<?>, GXFormSetting> commonSetting = settingRequest.toDaoCommonSetting();
			assertEquals("UTF-8", ((GTransformSetting)commonSetting.get(GTransformSetting.class)).getEncoding());
		}
	}
}
