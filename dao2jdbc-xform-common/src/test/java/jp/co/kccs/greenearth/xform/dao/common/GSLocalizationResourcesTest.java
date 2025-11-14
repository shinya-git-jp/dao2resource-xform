package jp.co.kccs.greenearth.xform.dao.common;

import jp.co.kccs.greenearth.commons.GFrameworkProperties;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.common.GSLocalizationResources;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public abstract class GSLocalizationResourcesTest {
	
	protected String dbScheme;
	
	protected GDbCommonSetting dbCommonSetting;
	
	@Before
	public void setup() throws Exception {
		Map<Class<? extends GXFormSetting>, GXFormSetting> xformSetting = getSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, xformSetting.get(GDbCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, xformSetting.get(GReservedWordSetting.class));
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, xformSetting.get(GLocaleCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, xformSetting.get(GTransformSetting.class));
		dbCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
		cleanInsert("jp/co/kccs/greenearth/xform/dao/common/GSLocalizationResourcesTest/import_message.xml");
	}

	protected abstract Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting();
	
	public void cleanInsert(String filePath)  {
		try {
			TestHelper.cleanInsert(dbCommonSetting, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void teardown() {
		GFrameworkProperties.refleshProperty();
	}
	
	@Test
	public void testGetMessage() {
		String message = GSLocalizationResources.getMessage("9507E3FD48FA48D28089A0BFDDC8B31C");
		Assert.assertEquals("junitGetMessage", message);
	}
	
	@Test
	public void testGetMessageWithParams() {
		String[] params = new String[]{"param1", "param2"};
		String message = GSLocalizationResources.getMessage("092CB25A54864970B87504A6EDA3BD47", params);
		Assert.assertEquals("junitGetMessageWithParams param1 param2", message);
	}
	
	@Test
	public void testGetMessageWithLocale() {
		String enMessage = GSLocalizationResources.getMessage("92148518D4CF41BDA449547D34A1851E", new Locale("en"));
		String jaMessage = GSLocalizationResources.getMessage("92148518D4CF41BDA449547D34A1851E", new Locale("ja"));
		String zhMessage = GSLocalizationResources.getMessage("92148518D4CF41BDA449547D34A1851E", new Locale("zh"));
		
		Assert.assertEquals("junitGetMessageWithLocale_EN", enMessage);
		Assert.assertEquals("junitGetMessageWithLocale_JA", jaMessage);
		Assert.assertEquals("junitGetMessageWithLocale_ZH", zhMessage);
	}
	
	@Test
	public void testGetMessageWithParamsAndLocale() {
		String enMessage = GSLocalizationResources.getMessage("AF27423E8E80473FB9896DBE0993C801", Arrays.asList("enParam1", "enParam2").toArray(new String[2]), new Locale("en"));
		String jaMessage = GSLocalizationResources.getMessage("AF27423E8E80473FB9896DBE0993C801", Arrays.asList("jaParam1", "jaParam2").toArray(new String[2]), new Locale("ja"));
		String zhMessage = GSLocalizationResources.getMessage("AF27423E8E80473FB9896DBE0993C801", Arrays.asList("zhParam1", "zhParam2").toArray(new String[2]), new Locale("zh"));
		
		Assert.assertEquals("junitGetMessageWithParamsAndLocale_EN enParam1 enParam2", enMessage);
		Assert.assertEquals("junitGetMessageWithParamsAndLocale_JA jaParam1 jaParam2", jaMessage);
		Assert.assertEquals("junitGetMessageWithParamsAndLocale_ZH zhParam1 zhParam2", zhMessage);
		
	}
//
//	@Test
//	public void testGetAllMessages() {
//		Map<Locale, Map<String, String>> messages = GSLocalizationResources.getAllMessages();
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
