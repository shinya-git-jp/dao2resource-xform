package jp.co.kccs.greenearth.xform.entity.jdbc;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSettingImpl;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;

public class TestUtils {
	public static void cleanInsert(String filePath, GDbCommonSetting dbCommonSetting)  {
		try {
			TestHelper.cleanInsert(dbCommonSetting, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static GDbCommonSetting getSuccessSetting() {
		return (GDbCommonSetting) TestHelper.getMySqlSetting().get(GDbCommonSetting.class);
	}
	public static GLocaleCommonSetting getLocaleCommonSetting() {
		return (GLocaleCommonSetting) TestHelper.getMySqlSetting().get(GLocaleCommonSetting.class);
	}
	public static GReservedWordSetting getReservedWordSetting() {
		return (GReservedWordSetting) TestHelper.getMySqlSetting().get(GReservedWordSetting.class);
	}
	public static GDbCommonSetting getFailedSetting() {
		GDbCommonSetting commonSetting = new GDbCommonSettingImpl();
		commonSetting.setDbType(GDaoDbType.mysql);
		commonSetting.setUrl("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
		commonSetting.setSchema("gef_jdbc_tool");
		commonSetting.setUsername("geframe");
		commonSetting.setPassword("Kccs0001");
		return commonSetting;
	}
}
