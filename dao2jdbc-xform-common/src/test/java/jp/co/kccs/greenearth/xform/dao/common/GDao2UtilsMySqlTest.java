package jp.co.kccs.greenearth.xform.dao.common;

import jp.co.kccs.greenearth.commons.GFrameworkProperties;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GDao2UtilsMySqlTest extends GDao2UtilsTest {



	@Override
	protected Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getMySqlSetting();
	}
	public String getSetCommonSettingFile() {
		return GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/common/GDao2UtilsTest/settings_setCommonSetting_mysql.yaml").getPath();
	}


	public String getDbType() {
		return GDaoDbType.mysql.getValue();
	}


	public void assertEqualBasicDataSource(BasicDataSource basicDataSource) {
		assertEquals(basicDataSource.getDriverClassName(), "com.mysql.cj.jdbc.Driver");
		assertEquals(GFrameworkProperties.getProperty("geframe.dao.DataSourceType"), "5");
	}
}
