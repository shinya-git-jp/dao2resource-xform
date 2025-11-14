package jp.co.kccs.greenearth.xform.framework.dao.core.sql.generator.impl;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.framework.dao.core.sql.generator.impl.GVeId2SqlGeneratorDeleteStrategyTest;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVeId2SqlGeneratorDeleteStrategyDB2Test extends GVeId2SqlGeneratorDeleteStrategyTest {
	
	@Override
	public Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getDb2SqlSetting();
	}
	
	protected String getDeleteSql_Generate(){
		return "DELETE FROM " + dbSchema +
				".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
				"WHERE jdbc_tool_test_pk_uk_mainmaster.objectID=?";
	}
}
