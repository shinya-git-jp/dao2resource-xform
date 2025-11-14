package jp.co.kccs.greenearth.xform.entity.dao.core.loader;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBEntity;
import jp.co.kccs.greenearth.framework.dao.enumtype.GDataSourceType;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GSEntityMetadataLoaderTest {
	
	protected String dbScheme;
	
	protected abstract GDataSourceType getDataSourceType();
	
	@Before
	public void setup() throws Exception {Map<Class<? extends GXFormSetting>, GXFormSetting> xformSetting = getSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, xformSetting.get(GDbCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, xformSetting.get(GReservedWordSetting.class));
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, xformSetting.get(GLocaleCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, xformSetting.get(GTransformSetting.class));
		dbScheme = GXFormSettingHolder.getSetting(GDbCommonSetting.class).getSchema();
		TestHelper.cleanInsert(GXFormSettingHolder.getSetting(GDbCommonSetting.class), "jp/co/kccs/greenearth/xform/entity/dao/core/loader/GSEntityMetadataLoaderTest/import_entity.xml");
	}

	protected abstract Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting();


	@Test
	public void testLoad() {
		String entityID = "CB30B5B47816426A8A14AFB44D6CA251";

		GEntityMetadataLoader<String, GDBEntity> loader = GFrameworkUtils.getComponent(GEntityMetadataLoader.class);
		GDBEntity dbEntity = loader.load(entityID);

		assertEquals(14, dbEntity.getColumns().size());
		assertEquals(dbScheme, dbEntity.getDataBaseName());
		assertEquals(getDataSourceType(), dbEntity.getDatabaseType());
		assertEquals("jdcb_tool_test_allColumntype", dbEntity.getEntityName());
		assertEquals("CB30B5B47816426A8A14AFB44D6CA251", dbEntity.getObjectID());
		assertEquals(3, dbEntity.getUniqueKeys().size());
		assertEquals("47C79083FBA149309517E5CA0A835B3D", dbEntity.getUniqueKeys().get(0).getObjectID());
		assertEquals(2, dbEntity.getUniqueKeys().get(0).getKeyElements().size());
		assertEquals("objectID", dbEntity.getUniqueKeys().get(0).getKeyElements().get(0).getColumnName());
		assertEquals("CompanyCD", dbEntity.getUniqueKeys().get(0).getKeyElements().get(1).getColumnName());
		assertEquals(3, dbEntity.getDBVirtualEntity().getForeignKeys().size());
		assertEquals(2, dbEntity.getDBVirtualEntity().getForeignKeys().get("95A0E9DB59BB408DB3E79ED5D9CDA82A").getForeignKeyElements().size());
	}
}
