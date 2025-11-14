package jp.co.kccs.greenearth.xform.entity.jdbc.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.entity.jdbc.TestUtils;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static jp.co.kccs.greenearth.xform.entity.jdbc.TestUtils.cleanInsert;
import static org.junit.Assert.*;

public class FetchEntityIdByDatabaseIdFunctionTest {

	/**
	 * [1] データベースIDが存在する場合、エンティティID一覧を返すこと。<br>
	 * [2] データベースIDが存在しない場合、空リストを返すこと。<br>
	 * [3] nullを渡す場合、全てデータベースID一覧を返すこと。<br>
	 * [4] 空文字列を渡す場合、全てデータベースID一覧を返すこと。<br>
	 * [5] データベースの接続が失敗する場合、エラーを返すこと。<br>
	 */
	@Test
	public void testHandle() {
		GFrameworkUtils.initDIContainer(null);
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, TestUtils.getSuccessSetting());
		cleanInsert("jp/co/kccs/greenearth/xform/entity/jdbc/import_entity.xml", TestUtils.getSuccessSetting());

		FetchEntityIdByDatabaseIdFunction fetchEntityIdByDatabaseIdFunction = new FetchEntityIdByDatabaseIdFunction();
		{
			List<String> result = fetchEntityIdByDatabaseIdFunction.handle("377BB20685E5497FB5B5209DACC76432");
			assertEquals(5, result.size());
		}
		{
			List<String> result = fetchEntityIdByDatabaseIdFunction.handle("377BB206Z5E5497FB5B5209DACC76432");
			assertTrue(result.isEmpty());
		}
		{
			List<String> result = fetchEntityIdByDatabaseIdFunction.handle(null);
			assertEquals(5, result.size());
		}
		{
			List<String> result = fetchEntityIdByDatabaseIdFunction.handle("");
			assertEquals(5, result.size());
		}
		{
			try {
				GFrameworkUtils.initDIContainer(null);
				GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, TestUtils.getFailedSetting());
				fetchEntityIdByDatabaseIdFunction.handle("");
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GFrameworkException);
			}
		}
	}
}
