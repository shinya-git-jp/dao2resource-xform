package jp.co.kccs.greenearth.xform.code.jdbc.service.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.xform.code.jdbc.service.TestUtils;
import jp.co.kccs.greenearth.xform.code.jdbc.service.resource.Dao2SqlResultResponse;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.code.jdbc.service.TestUtils.*;
import static org.junit.Assert.*;

public class SqlExtractorFunctionTest {
	/**
	 * [1] 仮想表IDが存在し、かつcrudTypesが指定される場合、仮想表IDに応じて、指定されたcrudTypesのSQLが生成されること。<br>
	 * [2] 仮想表IDが存在し、かつcrudTypesがnullの場合、仮想表IDに応じて、全てcrudTypesのSQLが生成されること。<br>
	 * [3] 仮想表IDが存在しない場合、生成が失敗し、エラーが発生うすること。<br>
	 * [4] 引数に必須フィールドが存在しない場合、エラーが発生すること。<br>
	 * [5] データベースが接続出来ない場合、エラーを返すこと。<br>
	 */
	@Test
	public void testHandle() {
		GDbCommonSetting commonSetting = TestUtils.getSuccessSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, TestUtils.getReservedWordSetting());
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, TestUtils.getLocaleCommonSetting());

		cleanInsert("jp/co/kccs/greenearth/xform/code/jdbc/service/function/import_extract_cud.xml", commonSetting);
		SqlExtractorFunction sqlExtractorFunction = new SqlExtractorFunction();
		{
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("veId", "1FEFE156727646DC9122045339BD3999");
			parameter.put("crudTypes", List.of("SELECT"));
			List<Dao2SqlResultResponse> result = sqlExtractorFunction.handle(parameter);
			try {
				String expectedValue = readFile("expected-GSqlExtractorFunction-case-1.txt");
				assertTrue(compare(expectedValue, result));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		{
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("veId", "1FEFE156727646DC9122045339BD3999");
			List<Dao2SqlResultResponse> result = sqlExtractorFunction.handle(parameter);
			try {
				String expectedValue = readFile("expected-GSqlExtractorFunction-case-2.txt");
				assertTrue(compare(expectedValue, result));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("veId", "35B6AB911C524EE69A30E29BF9A2904Z");
				List<Dao2SqlResultResponse> result = sqlExtractorFunction.handle(parameter);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GFrameworkException);
				assertEquals("java.lang.IllegalArgumentException: repository information is not found.", exception.getMessage());
			}
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				sqlExtractorFunction.handle(parameter);
				fail();
			} catch (Exception exception) {
				assertEquals(GFrameworkException.class, exception.getClass());
				assertEquals("veId parameters must be exist", exception.getMessage());
			}
		}
		{
			try {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("veId", "35B6AB911C524EE69A30E29BF9A2904Z");
				GFrameworkUtils.initDIContainer(null);
				commonSetting = TestUtils.getFailedSetting();
				GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
				List<Dao2SqlResultResponse> result = sqlExtractorFunction.handle(parameter);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GFrameworkException);
			}

		}
	}

}
