package jp.co.kccs.greenearth.xform.dao.common;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.common.GXFormSystemResourceDao;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.Map;

public abstract class GXFormSystemResourceDaoTest {
	
	protected String dbScheme;
	
	GDbCommonSetting dbCommonSetting;
	
	@Before
	public void setup() throws Exception {
		Map<Class<? extends GXFormSetting>, GXFormSetting> xformSetting = getSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, xformSetting.get(GDbCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, xformSetting.get(GReservedWordSetting.class));
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, xformSetting.get(GLocaleCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, xformSetting.get(GTransformSetting.class));
		dbCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
		cleanInsert("jp/co/kccs/greenearth/xform/dao/common/GXFormSystemResourceDaoTest/import_message.xml");
	}
	
	protected abstract Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting();
	
	public void cleanInsert(String filePath)  {
		try {
			TestHelper.cleanInsert(dbCommonSetting, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testFindResource() {
		GXFormSystemResourceDao dao = new GXFormSystemResourceDao();
		
		String enMessage = dao.findResource("92148518D4CF41BDA449547D34A1851E", new Locale("en"));
		String jaMessage = dao.findResource("92148518D4CF41BDA449547D34A1851E", new Locale("ja"));
		String zhMessage = dao.findResource("92148518D4CF41BDA449547D34A1851E", new Locale("zh"));
		
		Assert.assertEquals("junitGetMessageWithLocale_EN", enMessage);
		Assert.assertEquals("junitGetMessageWithLocale_JA", jaMessage);
		Assert.assertEquals("junitGetMessageWithLocale_ZH", zhMessage);
	}
	
//	@Test
//	public void testFindAllResource() {
//
//		GXFormSystemResourceDao dao = new GXFormSystemResourceDao();
//		Map<Locale, Map<String, String>> messages = dao.findResourceAll();
//		Assert.assertEquals(4, messages.size());
//		Map<String, String> defaultMessages = messages.get(new Locale(""));
//		Assert.assertEquals(defaultMessages.get("AF27423E8E80473FB9896DBE0993C801"), "junitGetMessageWithParamsAndLocale_EN {0} {1}");
//		Assert.assertEquals(defaultMessages.get("92148518D4CF41BDA449547D34A1851E"), "junitGetMessageWithLocale_EN");
//		Assert.assertEquals(defaultMessages.get("D9DB8458880A4AEA80FE75D5E1BB73B1"), null);
//		Assert.assertEquals(defaultMessages.get("092CB25A54864970B87504A6EDA3BD47"), "junitGetMessageWithParams {0} {1}");
//		Assert.assertEquals(defaultMessages.get("9507E3FD48FA48D28089A0BFDDC8B31C"), "junitGetMessage");
//
//		Map<String, String> enMessages = messages.get(new Locale("en"));
//		Assert.assertEquals(enMessages.get("AF27423E8E80473FB9896DBE0993C801"), "junitGetMessageWithParamsAndLocale_EN {0} {1}");
//		Assert.assertEquals(enMessages.get("92148518D4CF41BDA449547D34A1851E"), "junitGetMessageWithLocale_EN");
//		Assert.assertEquals(enMessages.get("D9DB8458880A4AEA80FE75D5E1BB73B1"), null);
//		Assert.assertEquals(enMessages.get("092CB25A54864970B87504A6EDA3BD47"), "junitGetMessageWithParams {0} {1}");
//		Assert.assertEquals(enMessages.get("9507E3FD48FA48D28089A0BFDDC8B31C"), "junitGetMessage");
//
//		Map<String, String> jaMessages = messages.get(new Locale("ja"));
//		Assert.assertEquals(jaMessages.get("AF27423E8E80473FB9896DBE0993C801"), "junitGetMessageWithParamsAndLocale_JA {0} {1}");
//		Assert.assertEquals(jaMessages.get("92148518D4CF41BDA449547D34A1851E"), "junitGetMessageWithLocale_JA");
//		Assert.assertEquals(jaMessages.get("D9DB8458880A4AEA80FE75D5E1BB73B1"), "geframe");
//		Assert.assertEquals(jaMessages.get("092CB25A54864970B87504A6EDA3BD47"), null);
//		Assert.assertEquals(jaMessages.get("9507E3FD48FA48D28089A0BFDDC8B31C"), null);
//
//		Map<String, String> zhMessages = messages.get(new Locale("zh"));
//		Assert.assertEquals(zhMessages.get("AF27423E8E80473FB9896DBE0993C801"), "junitGetMessageWithParamsAndLocale_ZH {0} {1}");
//		Assert.assertEquals(zhMessages.get("92148518D4CF41BDA449547D34A1851E"), "junitGetMessageWithLocale_ZH");
//		Assert.assertEquals(zhMessages.get("D9DB8458880A4AEA80FE75D5E1BB73B1"), null);
//		Assert.assertEquals(zhMessages.get("092CB25A54864970B87504A6EDA3BD47"), null);
//		Assert.assertEquals(zhMessages.get("9507E3FD48FA48D28089A0BFDDC8B31C"), null);
//	}
}
