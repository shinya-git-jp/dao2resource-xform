package jp.co.kccs.greenearth.xform.entity.jdbc.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.entity.jdbc.NotFoundException;
import jp.co.kccs.greenearth.xform.entity.jdbc.TestUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.entity.jdbc.TestUtils.cleanInsert;
import static org.junit.Assert.*;

public class EntityIdExistenceFunctionTest {

	/**
	 * [1] 仮想表IDが存在する場合、仮想表IDとタイプを返すこと。<br>
	 * [2] カテゴリIDが存在する場合、カテゴリIDとタイプを返すこと。<br>
	 * [3] 仮想表IDが存在しない場合、エラーが発生すること。<br>
	 * [4] カテゴリIDが存在しない場合、エラーが発生すること。<br>
	 * [5] 引数に必須フィールドが存在しない場合、エラーが発生すること。<br>
	 * [6] データベースが接続出来ない場合、エラーを返すこと。<br>
	 *
	 */
	@Test
	public void testHandle() {
		GFrameworkUtils.initDIContainer(null);
		cleanInsert("jp/co/kccs/greenearth/xform/entity/jdbc/import_entity.xml", TestUtils.getSuccessSetting());
		EntityIdExistenceFunction entityIdExistenceFunction = new EntityIdExistenceFunction();
		{
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("id", "2726BD6AA6254A1590612E092A91ACE4");
			parameter.put("type", "entityId");
			String result = entityIdExistenceFunction.handle(parameter);
			assertEquals("ID is exist", result);
		}
		{
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("id", "377BB20685E5497FB5B5209DACC76432");
			parameter.put("type", "databaseId");
			String result = entityIdExistenceFunction.handle(parameter);
			assertEquals("ID is exist", result);
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("id", "35B6AB911C524EE69A30E29BF9A2904Z");
				parameter.put("type", "entityId");
				entityIdExistenceFunction.handle(parameter);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof NotFoundException);
				assertEquals("Entity ID is not exist", exception.getMessage());
			}
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("id", "B27393A2779C472C88EEF38FABCFE11Z");
				parameter.put("type", "databaseId");
				entityIdExistenceFunction.handle(parameter);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof NotFoundException);
				assertEquals("Database ID is not exist or empty", exception.getMessage());
			}
		}
		{
			{
				try {
					Map<String, Object> parameter = new HashMap<>();
					parameter.put("id", "B27393A2779C472C88EEF38FABCFE118");
					entityIdExistenceFunction.handle(parameter);
					fail();
				} catch (Exception exception) {
					assertEquals(GFrameworkException.class, exception.getClass());
					assertEquals("id and type parameters must be exist", exception.getMessage());
				}
			}
			{
				try {
					Map<String, Object> parameter = new HashMap<>();
					parameter.put("type", "databaseId");
					entityIdExistenceFunction.handle(parameter);
					fail();
				} catch (Exception exception) {
					assertEquals(GFrameworkException.class, exception.getClass());
					assertEquals("id and type parameters must be exist", exception.getMessage());
				}
			}
			{
				try {
					Map<String, Object> parameter = new HashMap<>();
					parameter.put("id", "B27393A2779C472C88EEF38FABCFE11Z");
					parameter.put("type", "databaseId");
					GFrameworkUtils.initDIContainer(null);
					GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, TestUtils.getFailedSetting());
					entityIdExistenceFunction.handle(parameter);
					fail();
				} catch (Exception exception) {
					assertTrue(exception instanceof RuntimeException);
				}
			}
		}
	}
}
