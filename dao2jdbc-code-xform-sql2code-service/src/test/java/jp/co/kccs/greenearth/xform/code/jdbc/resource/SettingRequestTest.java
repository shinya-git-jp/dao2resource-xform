package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jp.co.kccs.greenearth.xform.code.jdbc.configuration.GCodeTransformSetting;
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
			Map<String, Object> codeTransform = new HashMap<>();
			codeTransform.put("useForeignKey", true);
			codeTransform.put("forceAliasColumn", false);
			codeTransform.put("entityQuery", false);
			codeTransform.put("useExpMap", true);


			SettingRequest settingRequest = new SettingRequest();
			settingRequest.setTransform(transform);
			settingRequest.setCodeTransform(codeTransform);
			Map<Class<?>, GXFormSetting> commonSetting = settingRequest.toDaoCommonSetting();
			assertEquals("UTF-16", ((GTransformSetting)commonSetting.get(GTransformSetting.class)).getEncoding());
			assertTrue(((GCodeTransformSetting)commonSetting.get(GCodeTransformSetting.class)).isUseForeignKey());
			assertTrue(((GCodeTransformSetting)commonSetting.get(GCodeTransformSetting.class)).isUseExpMap());
			assertFalse(((GCodeTransformSetting)commonSetting.get(GCodeTransformSetting.class)).isForceAliasColumn());
			assertFalse(((GCodeTransformSetting)commonSetting.get(GCodeTransformSetting.class)).isEntityQuery());


		}
		{
			SettingRequest settingRequest = new SettingRequest();
			Map<Class<?>, GXFormSetting> commonSetting = settingRequest.toDaoCommonSetting();
			assertEquals("UTF-8", ((GTransformSetting)commonSetting.get(GTransformSetting.class)).getEncoding());
			assertFalse(((GCodeTransformSetting)commonSetting.get(GCodeTransformSetting.class)).isUseForeignKey());
			assertFalse(((GCodeTransformSetting)commonSetting.get(GCodeTransformSetting.class)).isUseExpMap());
			assertFalse(((GCodeTransformSetting)commonSetting.get(GCodeTransformSetting.class)).isForceAliasColumn());
			assertTrue(((GCodeTransformSetting)commonSetting.get(GCodeTransformSetting.class)).isEntityQuery());

		}
	}
}
