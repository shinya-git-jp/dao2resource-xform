package jp.co.kccs.greenearth.xform.entity.dao.core.loader;

import jp.co.kccs.greenearth.framework.dao.enumtype.GDataSourceType;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;

import java.util.Map;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GSEntityMetadataLoaderOracleTest extends GSEntityMetadataLoaderTest {

	@Override
	protected Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting() {
		return TestHelper.getOracleSqlSetting();
	}

	@Override
	protected GDataSourceType getDataSourceType() {
		return GDataSourceType.ORACLE;
	}
}
