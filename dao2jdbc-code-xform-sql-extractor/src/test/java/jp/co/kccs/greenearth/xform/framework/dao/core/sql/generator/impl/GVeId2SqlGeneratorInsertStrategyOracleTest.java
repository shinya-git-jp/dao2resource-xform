package jp.co.kccs.greenearth.xform.framework.dao.core.sql.generator.impl;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;

import java.util.Map;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVeId2SqlGeneratorInsertStrategyOracleTest extends GVeId2SqlGeneratorInsertStrategyTest {

	@Override
	public Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getOracleSqlSetting();
	}

	@Override
	public String getTimestampCommand() {
		return "SYSDATE";
	}
}
