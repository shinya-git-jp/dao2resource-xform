package jp.co.kccs.greenearth.xform.code.jdbc.service.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.code.jdbc.service.NotFoundException;
import jp.co.kccs.greenearth.xform.code.jdbc.service.TestUtils;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.code.jdbc.service.TestUtils.cleanInsert;
import static org.junit.Assert.*;

public class VeIdExistenceFunctionTest {

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
		GDbCommonSetting commonSetting = TestUtils.getSuccessSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, TestUtils.getReservedWordSetting());
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, TestUtils.getLocaleCommonSetting());

		cleanInsert("jp/co/kccs/greenearth/xform/code/jdbc/service/function/import.xml", commonSetting);
		VeIdExistenceFunction veIdExistenceFunction = new VeIdExistenceFunction();
		{
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("id", "0AF1C308DE0848E9AB8FFD11D4EEC32E");
			parameter.put("type", "veId");
			String result = veIdExistenceFunction.handle(parameter);
			assertEquals("ID is exist.", result);
		}
		{
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("id", "B27393A2779C472C88EEF38FABCFE118");
			parameter.put("type", "categoryId");
			String result = veIdExistenceFunction.handle(parameter);
			assertEquals("ID is exist.", result);
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("id", "35B6AB911C524EE69A30E29BF9A2904Z");
				parameter.put("type", "veId");
				veIdExistenceFunction.handle(parameter);
			} catch (Exception exception) {
				assertTrue(exception instanceof NotFoundException);
				assertEquals("Virtual Entity ID is not exist", exception.getMessage());
			}
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("id", "B27393A2779C472C88EEF38FABCFE11Z");
				parameter.put("type", "categoryId");
				veIdExistenceFunction.handle(parameter);
			} catch (Exception exception) {
				assertTrue(exception instanceof NotFoundException);
				assertEquals("Category ID is not exist", exception.getMessage());
			}
		}
		{
			{
				try {
					Map<String, Object> parameter = new HashMap<>();
					parameter.put("id", "B27393A2779C472C88EEF38FABCFE118");
					veIdExistenceFunction.handle(parameter);
					fail();
				} catch (Exception exception) {
					assertEquals(GFrameworkException.class, exception.getClass());
					assertEquals("id and type parameters must be exist", exception.getMessage());
				}
			}
			{
				try {
					Map<String, Object> parameter = new HashMap<>();
					parameter.put("type", "categoryId");
					veIdExistenceFunction.handle(parameter);
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
					parameter.put("type", "categoryId");
					GFrameworkUtils.initDIContainer(null);
					commonSetting = TestUtils.getFailedSetting();
					GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
					veIdExistenceFunction.handle(parameter);
				} catch (Exception exception) {
					assertTrue(exception instanceof NotFoundException);
				}
			}
		}
	}
}
