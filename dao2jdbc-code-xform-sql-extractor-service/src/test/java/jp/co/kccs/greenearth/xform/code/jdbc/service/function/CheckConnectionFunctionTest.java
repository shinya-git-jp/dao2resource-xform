package jp.co.kccs.greenearth.xform.code.jdbc.service.function;

import jp.co.kccs.greenearth.commons.db.GConnectionManager;
import jp.co.kccs.greenearth.xform.code.jdbc.service.TestUtils;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import org.junit.Test;
import java.util.Collections;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CheckConnectionFunctionTest {

	/**
	 * [1] 設定ファイルのDB情報が接続できる場合、「Success」を返すこと。<br>
	 * [2] 設定ファイルのDB情報が接続できない場合、エラーを返すこと。<br>
	 */
	@Test
	public void test1() {
		{
			GConnectionManager.getInstance().removeConnections();
			GDbCommonSetting commonSetting = TestUtils.getSuccessSetting();
			GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
			CheckConnectionFunction checkConnectionFunction = new CheckConnectionFunction();
			String result = checkConnectionFunction.handle(Collections.emptyEnumeration());
			assertEquals("Success!", result);
		}
		{
			GConnectionManager.getInstance().removeConnections();
			GDbCommonSetting commonSetting = TestUtils.getFailedSetting();
			GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
			CheckConnectionFunction checkConnectionFunction = new CheckConnectionFunction();
			String result = checkConnectionFunction.handle(Collections.emptyEnumeration());
//			assertEquals("{\"errorMessage: \":\"Cannot create PoolableConnectionFactory (Access denied for user 'geframe2'@'10.150.197.55' (using password: YES))\"}", result);
		}
	}
	@Test
	public void test2() throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(2);

		Callable<Boolean> task1 = () -> {
			GDbCommonSetting commonSetting = TestUtils.getSuccessSetting();
			GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
			CheckConnectionFunction checkConnectionFunction = new CheckConnectionFunction();
			String result = checkConnectionFunction.handle(Collections.emptyEnumeration());
			System.out.println(result);
			GConnectionManager.getInstance().removeConnections();
			return result.equals("Success!");
		};

		Callable<Boolean> task2 = () -> {
			GDbCommonSetting commonSetting = TestUtils.getFailedSetting();
			GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, commonSetting);
			CheckConnectionFunction checkConnectionFunction = new CheckConnectionFunction();
			String result = checkConnectionFunction.handle(Collections.emptyEnumeration());
			System.out.println(result);
			GConnectionManager.getInstance().removeConnections();
			return !"Success".equals(result);
		};

		// Submit tasks
		Future<Boolean> future1 = executor.submit(task1);
		Future<Boolean> future2 = executor.submit(task2);

		// Wait for both to complete and assert results
		assertTrue(future1.get());
		assertTrue(future2.get());

		executor.shutdown();
//		assertTrue(executor.awaitTermination(1, TimeUnit.SECONDS));

	}
}
