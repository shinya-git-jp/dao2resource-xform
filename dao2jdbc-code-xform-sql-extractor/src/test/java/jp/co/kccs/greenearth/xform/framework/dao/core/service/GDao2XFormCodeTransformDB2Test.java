package jp.co.kccs.greenearth.xform.framework.dao.core.service;


import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.framework.dao.core.service.GDao2XFormCodeTransformTest;

import java.util.Map;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GDao2XFormCodeTransformDB2Test extends GDao2XFormCodeTransformTest {

	@Override
	public Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getDb2SqlSetting();
	}

	@Override
	protected String getTimestampCommand() {
		return "CURRENT TIMESTAMP";
	}


	protected String getDeleteSql_TransformByVeId() {
		return "DELETE FROM" + System.lineSeparator() + "    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster";
	}

	protected String getDeleteSql_TransformByVeCategoryId() {
		return "DELETE FROM" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
						"WHERE" + System.lineSeparator() +
						"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
						"    AND (" + System.lineSeparator() +
						"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
						"    )";
	}
	protected String getDeleteSql_TransformByVeCategoryId_2() {
		return "DELETE FROM" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
						"WHERE" + System.lineSeparator() +
						"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
						"    AND (" + System.lineSeparator() +
						"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
						"    )";
	}
}
