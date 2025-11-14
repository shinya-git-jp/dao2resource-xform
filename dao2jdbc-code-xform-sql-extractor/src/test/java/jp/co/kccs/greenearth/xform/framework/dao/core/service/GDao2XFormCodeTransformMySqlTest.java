package jp.co.kccs.greenearth.xform.framework.dao.core.service;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import java.util.Map;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GDao2XFormCodeTransformMySqlTest extends GDao2XFormCodeTransformTest {
	@Override
	public Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getMySqlSetting();
	}

	@Override
	protected String getTimestampCommand() {
		return "CURRENT_TIMESTAMP";
	}
}
