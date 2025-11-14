package jp.co.kccs.greenearth.xform.dao.common;

import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;

import java.util.Map;

public class GXFormSystemResourceDaoDB2Test extends GXFormSystemResourceDaoTest {

	@Override
	protected Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getDb2SqlSetting();
	}
}
