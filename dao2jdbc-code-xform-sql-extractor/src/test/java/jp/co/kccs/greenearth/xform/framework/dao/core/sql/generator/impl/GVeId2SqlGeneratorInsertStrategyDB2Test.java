package jp.co.kccs.greenearth.xform.framework.dao.core.sql.generator.impl;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.framework.dao.core.sql.generator.impl.GVeId2SqlGeneratorInsertStrategyTest;

import java.util.Map;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVeId2SqlGeneratorInsertStrategyDB2Test extends GVeId2SqlGeneratorInsertStrategyTest {

	@Override
	public Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getDb2SqlSetting();
	}

	@Override
	public String getTimestampCommand() {
		return "CURRENT TIMESTAMP";
	}
}
