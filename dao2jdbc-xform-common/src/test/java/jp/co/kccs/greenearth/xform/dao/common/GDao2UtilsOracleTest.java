package jp.co.kccs.greenearth.xform.dao.common;

import jp.co.kccs.greenearth.commons.GFrameworkProperties;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GDao2UtilsOracleTest extends GDao2UtilsTest {


	@Override
	protected Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getOracleSqlSetting();
	}
	public String getSetCommonSettingFile() {
		return GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/common/GDao2UtilsTest/settings_setCommonSetting_oracle.yaml").getPath();
	}


	public String getDbType() {
		return GDaoDbType.oracle.getValue();
	}


	public void assertEqualBasicDataSource(BasicDataSource basicDataSource) {
		assertEquals(basicDataSource.getDriverClassName(), "oracle.jdbc.driver.OracleDriver");
		assertEquals(GFrameworkProperties.getProperty("geframe.dao.DataSourceType"), "0");
	}
}
