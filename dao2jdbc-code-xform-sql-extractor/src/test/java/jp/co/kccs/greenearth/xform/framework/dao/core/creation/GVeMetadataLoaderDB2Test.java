package jp.co.kccs.greenearth.xform.framework.dao.core.creation;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.dao.common.GVECategoryImpl;
import jp.co.kccs.greenearth.xform.dao.common.GVEItemImpl;
import jp.co.kccs.greenearth.xform.framework.dao.core.creation.GVeMetadataLoaderTest;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVeMetadataLoaderDB2Test extends GVeMetadataLoaderTest {


	@Override
	public Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getDb2SqlSetting();
	}
}
