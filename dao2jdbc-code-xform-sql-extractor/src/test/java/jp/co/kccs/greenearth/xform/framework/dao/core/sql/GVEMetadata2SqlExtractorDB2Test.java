package jp.co.kccs.greenearth.xform.framework.dao.core.sql;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import java.util.Map;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVEMetadata2SqlExtractorDB2Test extends GVEMetadata2SqlExtractorTest {
	@Override
	public Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getDb2SqlSetting();
	}
	
	@Override
	protected String getTimestampCommand() {
		return "CURRENT TIMESTAMP";
	}
	
	protected String getDeleteSql_Extract_inq() {
		return "DELETE FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster WHERE jdbc_tool_test_pk_uk_mainmaster.objectID=?";
	}
	
	protected String getDeleteSql_Extract_reg() {
		return "DELETE FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
				"WHERE jdbc_tool_test_pk_uk_mainmaster.objectID=?";
	}
}
