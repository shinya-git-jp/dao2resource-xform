package jp.co.kccs.greenearth.xform.entity.jdbc.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.entity.jdbc.TestUtils;
import jp.co.kccs.greenearth.xform.entity.jdbc.utility.Utils;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.entity.jdbc.TestUtils.cleanInsert;
import static org.junit.Assert.*;

public class EntityExtractorFunctionTest {

	/**
	 * [1] エンティティIDが存在し、エンティティIDに応じて、エンティティの情報が抽出されること。<br>
	 * [2] エンティティIDが存在しない場合、生成が失敗し、エラーが発生うすること。<br>
	 * [4] 引数に必須フィールドが存在しない場合、エラーが発生すること。<br>
	 * [5] データベースが接続出来ない場合、エラーを返すこと。<br>
	 */
	@Test
	public void testHandle() {
		GFrameworkUtils.initDIContainer(null);
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, TestUtils.getSuccessSetting());
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, TestUtils.getLocaleCommonSetting());
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, TestUtils.getReservedWordSetting());
		cleanInsert("jp/co/kccs/greenearth/xform/entity/jdbc/import_entity.xml", TestUtils.getSuccessSetting());

		EntityExtractorFunction entityExtractorFunction = new EntityExtractorFunction();
		{
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("entityId", "2726BD6AA6254A1590612E092A91ACE4");
			GDao2VirtualEntity.Entity result = entityExtractorFunction.handle(parameter);
			String resultString = Utils.convertObjectToString(result);
			assertEquals("{\"name\":\"SLocalization\",\"phyName\":null,\"databaseName\":\"gef_jdbc_tool\",\"primaryKey\":{\"name\":\"objectID\",\"phyName\":null,\"size\":32,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":true,\"primaryKey\":true},\"columns\":[{\"name\":\"objectID\",\"phyName\":null,\"size\":32,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":true,\"primaryKey\":true},{\"name\":\"country1\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false},{\"name\":\"country2\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false},{\"name\":\"country3\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false},{\"name\":\"country4\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false},{\"name\":\"country5\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false}],\"uniqueKeys\":[],\"foreignKeys\":[]}", resultString);
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("entityId", "35B6AB911C524EE69A30E29BF9A2904Z");
				entityExtractorFunction.handle(parameter);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GFrameworkException);
				assertEquals("java.lang.IllegalArgumentException: repository information is not found.", exception.getMessage());
			}
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				entityExtractorFunction.handle(parameter);
				fail();
			} catch (Exception exception) {
				assertEquals(GFrameworkException.class, exception.getClass());
				assertEquals("entityId parameters must be exist", exception.getMessage());
			}
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("entityId", "35B6AB911C524EE69A30E29BF9A2904Z");
				GFrameworkUtils.initDIContainer(null);
				GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, TestUtils.getFailedSetting());
				entityExtractorFunction.handle(parameter);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GFrameworkException);
			}
		}
	}

}
