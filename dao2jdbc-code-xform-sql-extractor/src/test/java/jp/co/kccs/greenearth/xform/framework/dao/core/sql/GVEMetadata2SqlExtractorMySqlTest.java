package jp.co.kccs.greenearth.xform.framework.dao.core.sql;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import java.util.Map;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVEMetadata2SqlExtractorMySqlTest extends GVEMetadata2SqlExtractorTest {
	@Override
	public Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getMySqlSetting();
	}

	@Override
	protected String getTimestampCommand() {
		return "CURRENT_TIMESTAMP";
	}
}
