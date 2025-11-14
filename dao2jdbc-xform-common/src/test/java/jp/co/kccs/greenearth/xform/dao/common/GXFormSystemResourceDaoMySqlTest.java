package jp.co.kccs.greenearth.xform.dao.common;

import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import org.junit.Before;

import java.util.Map;

public class GXFormSystemResourceDaoMySqlTest extends GXFormSystemResourceDaoTest {

	@Override
	protected Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getMySqlSetting();
	}
}
