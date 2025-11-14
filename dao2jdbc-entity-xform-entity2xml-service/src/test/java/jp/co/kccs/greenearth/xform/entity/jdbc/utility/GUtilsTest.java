package jp.co.kccs.greenearth.xform.entity.jdbc.utility;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GUtilsTest {

	/**
	 * [1] 文字列のフォーマットがJSONの場合、変換が成功すること。<br>
	 * [2] 文字列のフォーマットがJSONじゃない場合、変換が失敗すること。<br>
	 */
	@Test
	public void testParseStringToMap() {
		{
			String str = "{ \"test\": \"value\"}";
			Map<String, Object> map = GUtils.parseStringToMap(str);
			assertEquals("value", map.get("test"));
		}
		{
			try {
				String str = "{";
				GUtils.parseStringToMap(str);
				fail();
			} catch (Exception exception) {
				assertEquals(GFrameworkException.class, exception.getClass());
			}
		}
	}


	/**
	 * [1] インプットが{@link Map}型の場合、変換されたオブジェクトがJSON文字列になること。<br>
	 */
	@Test
	public void testConvertMapToString() {
		{
			Map<String, Object> map = new HashMap<>();
			map.put("test", "value");
			assertEquals("{\"test\":\"value\"}", GUtils.convertObjectToString(map));
		}

	}
}
