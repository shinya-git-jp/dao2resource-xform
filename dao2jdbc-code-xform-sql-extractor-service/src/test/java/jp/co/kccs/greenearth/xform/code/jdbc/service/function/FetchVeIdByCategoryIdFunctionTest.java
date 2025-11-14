package jp.co.kccs.greenearth.xform.code.jdbc.service.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.code.jdbc.service.TestUtils;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.Test;
import java.util.List;

import static jp.co.kccs.greenearth.xform.code.jdbc.service.TestUtils.cleanInsert;
import static org.junit.Assert.*;

public class FetchVeIdByCategoryIdFunctionTest {


	/**
	 * [1] カテゴリIDが存在する場合、仮想表ID一覧を返すこと。<br>
	 * [2] カテゴリIDが存在しない場合、空リストを返すこと。<br>
	 * [3] nullを渡す場合、全て仮想表ID一覧を返すこと。<br>
	 * [4] 空文字列を渡す場合、全て仮想表ID一覧を返すこと。<br>
	 * [5] データベースの接続が失敗する場合、エラーを返すこと。<br>
	 */
	@Test
	public void testHandle() {
		GDbCommonSetting commonSetting = TestUtils.getSuccessSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, TestUtils.getReservedWordSetting());
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, TestUtils.getLocaleCommonSetting());
		cleanInsert("jp/co/kccs/greenearth/xform/code/jdbc/service/function/import.xml", commonSetting);
		FetchVeIdByCategoryIdFunction fetchVeIdByCategoryIdFunction = new FetchVeIdByCategoryIdFunction();
		GFrameworkUtils.initDIContainer(null);
		{
			List<String> result = fetchVeIdByCategoryIdFunction.handle("B27393A2779C472C88EEF38FABCFE118");
			assertEquals(1, result.size());
		}
		{
			List<String> result = fetchVeIdByCategoryIdFunction.handle("3083F38BA88F4279957254F992E7D5AZ");
			assertTrue(result.isEmpty());
		}
		{
			List<String> result = fetchVeIdByCategoryIdFunction.handle(null);
			assertEquals(3, result.size());
		}
		{
			List<String> result = fetchVeIdByCategoryIdFunction.handle("");
			assertEquals(3, result.size());
		}
		{
			GFrameworkUtils.initDIContainer(null);
			commonSetting = TestUtils.getFailedSetting();
			GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
			try {
				fetchVeIdByCategoryIdFunction.handle("");
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GFrameworkException);
			}
		}
	}
}
